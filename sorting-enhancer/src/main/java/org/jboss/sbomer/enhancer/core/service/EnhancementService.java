package org.jboss.sbomer.enhancer.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.jboss.sbomer.enhancer.core.port.spi.FailureNotifier;
import org.jboss.sbomer.enhancer.core.port.spi.ResultPublisher;
import org.jboss.sbomer.enhancer.core.port.spi.SbomStorage;
import org.jboss.sbomer.enhancer.core.utility.FailureUtility;
import org.jboss.sbomer.events.kafka.common.EnhancerSpec;
import org.jboss.sbomer.events.kafka.dispatcher.GenerationCreated;
import org.jboss.sbomer.events.kafka.enhancer.EnhancementFinished;
import org.jboss.sbomer.events.kafka.generator.GenerationFinished;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.jboss.sbomer.enhancer.core.ApplicationConstants.COMPONENT_NAME;

@ApplicationScoped
@Slf4j
public class EnhancementService {

    // These are the "plugs" our brain needs to use.
    private final SbomStorage sbomStorage;
    private final ResultPublisher resultPublisher;
    private final FailureNotifier failureNotifier;
    private final ObjectMapper objectMapper;

    @Inject
    public EnhancementService(SbomStorage sbomStorage, ResultPublisher resultPublisher, FailureNotifier failureNotifier, ObjectMapper objectMapper) {
        this.sbomStorage = sbomStorage;
        this.resultPublisher = resultPublisher;
        this.failureNotifier = failureNotifier;
        this.objectMapper = objectMapper;
    }

    /**
     * Main entry point for processing an enhancement from a Generator.
     */
    public void enhance(GenerationFinished event) {
        try {
            GenerationCreated originalEvent = event.getData().getOriginalEvent();
            List<EnhancerSpec> recipeEnhancers = originalEvent.getGenerationData().getRecipe().getEnhancers();

            // Business Rule: Am I the first enhancer in the chain?
            if (isMyTurn(recipeEnhancers, null)) {
                log.info("I am the first enhancer for Generation ID '{}'. Starting work.", originalEvent.getGenerationData().getGenerationRequest().getGenerationId());
                List<String> inputUrls = event.getData().getBaseSbomUrls();
                performEnhancement(originalEvent, inputUrls);
            }
        } catch (Exception e) {
            failureNotifier.notify(FailureUtility.buildFailureSpecFromException(e), event);
        }
    }

    /**
     * Main entry point for processing an enhancement from another Enhancer.
     */
    public void enhance(EnhancementFinished event) {
        try {
            GenerationCreated originalEvent = event.getData().getOriginalEvent();
            List<EnhancerSpec> recipeEnhancers = originalEvent.getGenerationData().getRecipe().getEnhancers();

            // Business Rule: Am I the next enhancer in the chain?
            if (isMyTurn(recipeEnhancers, event.getData().getLastEnhancement())) {
                log.info("It is my turn to enhance Generation ID '{}'. Starting work.", originalEvent.getGenerationData().getGenerationRequest().getGenerationId());
                // The input for this step is the output from the last step.
                List<String> inputUrls = event.getData().getLastEnhancedSbomUrls();
                performEnhancement(originalEvent, inputUrls);
            }
        } catch (Exception e) {
            failureNotifier.notify(FailureUtility.buildFailureSpecFromException(e), event);
        }
    }

    private void performEnhancement(GenerationCreated originalEvent, List<String> inputUrls) throws Exception {
        // 1. Download the latest SBOMs.
        List<String> sbomContents = sbomStorage.download(inputUrls);

        // 2. Perform the actual business logic: sorting.
        List<String> sortedSbomContents = sortComponents(sbomContents);

        // 3. Upload the new, sorted SBOMs and get their new URLs.
        List<String> newSbomUrls = sbomStorage.upload(sortedSbomContents);

        // 4. Business Rule: Am I the last enhancer in the chain?
        List<EnhancerSpec> recipe = originalEvent.getGenerationData().getRecipe().getEnhancers();
        if (isLastEnhancerInChain(recipe)) {
            resultPublisher.publishFinal(originalEvent, newSbomUrls);
        } else {
            resultPublisher.publishFinished(originalEvent, newSbomUrls);
        }
    }

    // --- Private helper methods for business rules (Now Implemented) ---

    private boolean isMyTurn(List<EnhancerSpec> recipe, String lastEnhancer) {
        if (lastEnhancer == null) {
            // This is the first enhancement step. Is it me?
            return !recipe.isEmpty() && COMPONENT_NAME.equals(recipe.get(0).getName());
        }

        // Find the index of the last enhancer that ran.
        for (int i = 0; i < recipe.size(); i++) {
            if (recipe.get(i).getName().equals(lastEnhancer)) {
                // Check if there is a next step and if that next step is me.
                int nextIndex = i + 1;
                if (nextIndex < recipe.size()) {
                    return COMPONENT_NAME.equals(recipe.get(nextIndex).getName());
                }
            }
        }
        return false; // Not my turn.
    }

    private boolean isLastEnhancerInChain(List<EnhancerSpec> recipe) {
        if (recipe.isEmpty()) {
            return false;
        }
        // Am I the last one in the list?
        return COMPONENT_NAME.equals(recipe.get(recipe.size() - 1).getName());
    }

    /**
     * A realistic simulation of sorting the 'components' array in a CycloneDX SBOM.
     */
    private List<String> sortComponents(List<String> sbomContents) throws Exception {
        List<String> sortedSbomContents = new ArrayList<>();

        for (String content : sbomContents) {
            log.info("Sorting components alphabetically in SBOM...");
            JsonNode root = objectMapper.readTree(content);

            // Navigate to the 'components' array
            JsonNode componentsNode = root.path("components");
            if (componentsNode.isArray()) {
                ArrayNode componentsArray = (ArrayNode) componentsNode;
                List<JsonNode> components = new ArrayList<>();
                componentsArray.forEach(components::add);

                // Sort the list of components alphabetically by 'name'
                components.sort(Comparator.comparing(c -> c.path("name").asText()));

                // Clear and rebuild the original array
                componentsArray.removeAll();
                components.forEach(componentsArray::add);
            }

            // Convert the modified JSON tree back to a string
            sortedSbomContents.add(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root));
        }
        return sortedSbomContents;
    }
}