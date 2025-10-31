package org.jboss.sbomer.generation.service.core.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.jboss.sbomer.events.kafka.generator.GenerationUpdate;
import org.jboss.sbomer.events.kafka.generator.UpdateDataSpec;
import org.jboss.sbomer.generation.service.core.domain.dto.GenerationRecord;
import org.jboss.sbomer.generation.service.core.domain.enums.GenerationResult;
import org.jboss.sbomer.generation.service.core.domain.enums.GenerationStatus;
import org.jboss.sbomer.generation.service.core.port.api.GenerationProcessor;
import org.jboss.sbomer.generation.service.core.port.api.GenerationStatusProcessor;
import org.jboss.sbomer.generation.service.core.port.spi.FailureNotifier;
import org.jboss.sbomer.generation.service.core.port.spi.GenerationRepository;
import org.jboss.sbomer.generation.service.core.port.spi.GenerationScheduler;
import org.jboss.sbomer.generation.service.core.utility.FailureUtility;
import org.jboss.sbomer.events.kafka.common.*;
import org.jboss.sbomer.events.kafka.dispatcher.GenerationCreated;
import org.jboss.sbomer.events.kafka.dispatcher.GenerationDataSpec;
import org.jboss.sbomer.events.kafka.dispatcher.RecipeSpec;
import org.jboss.sbomer.events.kafka.handler.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.jboss.sbomer.generation.service.core.ApplicationConstants.COMPONENT_NAME;

@ApplicationScoped
@Slf4j
public class GenerationService implements GenerationProcessor, GenerationStatusProcessor {

    GenerationScheduler generationScheduler;
    GenerationRepository generationRepository;
    FailureNotifier failureNotifier;

    @Inject
    public GenerationService(GenerationScheduler generationScheduler, GenerationRepository generationRepository, FailureNotifier failureNotifier) {
        this.generationRepository = generationRepository;
        this.generationScheduler = generationScheduler;
        this.failureNotifier = failureNotifier;
    }

    @Override
    public void processGenerations(RequestsCreated event) {
        RequestDataSpec requestData = event.getRequestData();
        log.info("Dispatching {} individual generation requests for RequestId '{}'.",
                requestData.getGenerationRequests().size(), requestData.getRequestId());

        // --- The try-catch now wraps the entire loop ---
        try {
            // For each request in the batch...
            for (var originalRequest : requestData.getGenerationRequests()) {

                // 1. Build the recipe. If this fails, it will be caught by the block below.
                RecipeSpec recipe = buildRecipeFor(originalRequest.getTarget());

                // 2. Create the event payload.
                GenerationDataSpec generationData = GenerationDataSpec.newBuilder()
                        .setRequestId(requestData.getRequestId())
                        .setGenerationRequest(originalRequest)
                        .setRecipe(recipe)
                        .build();

                GenerationCreated generationEvent = GenerationCreated.newBuilder()
                        .setContext(createNewContext())
                        .setGenerationData(generationData)
                        .build();

                // 3. Publish the event. If this fails, it will also be caught.
                generationScheduler.schedule(generationEvent);
            }
        } catch (Exception e) {
            // If ANY step inside the loop fails for ANY request...
            log.error("Failed to dispatch batch for RequestId '{}' due to an error. Halting processing. Error: {}",
                    requestData.getRequestId(), e.getMessage(), e);

            FailureSpec failure = FailureUtility.buildFailureSpecFromException(e);

            // Notify the failure system with the full context.
            failureNotifier.notify(failure, event);

            // Exit the method immediately, stopping any further processing.
            return;
        }
    }


    /**
     * The "Recipe Book" - this is where the decision logic lives.
     */
    private RecipeSpec buildRecipeFor(TargetSpec target) {
        GeneratorSpec generator;
        List<EnhancerSpec> enhancers = new ArrayList<>();

        // Example logic: choose a different generator based on the target type
        if ("RPM".equals(target.getType())) {
            generator = GeneratorSpec.newBuilder()
                    .setName("cyclonedx-maven-plugin")
                    .setVersion("2.7.9")
                    .build();
            // Maybe RPMs get a special enhancer
            enhancers.add(EnhancerSpec.newBuilder().setName("rpm-enhancer").setVersion("1.0.0").build());

        } else if ("CONTAINER_IMAGE".equals(target.getType())) {
            generator = GeneratorSpec.newBuilder()
                    .setName("syft-generator")
                    .setVersion("1.5.0")
                    .build();
            enhancers.add(EnhancerSpec.newBuilder().setName("sorting-enhancer").setVersion("1.0.0").build());
        } else {
            // Default or throw an error for unsupported types
            throw new IllegalArgumentException("Unsupported target type: " + target.getType());
        }

        return RecipeSpec.newBuilder()
                .setGenerator(generator)
                .setEnhancers(enhancers)
                .build();
    }

    private ContextSpec createNewContext() {
        ContextSpec context = ContextSpec.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setSource(COMPONENT_NAME)
                .setTimestamp(Instant.now())
                .setSpecVersion("1.0")
                .build();
        return context;
    }

    @Override
    public void processStatusUpdate(GenerationUpdate generationUpdate) {
        UpdateDataSpec updateData = generationUpdate.getUpdateData();
        String generationId = updateData.getGenerationId();

        // 1. Fetch the existing record from the database.
        GenerationRecord record = generationRepository.findById(generationId);

        // 2. Handle the case where no record is found.
        // This is important for system stability.
        if (record == null) {
            log.error("Received a status update for a non-existent generation ID: {}. Discarding event.", generationId);
            return;
        }

        // 3. Map the event data to the GenerationRecord domain object.
        log.info("Updating GenerationRecord '{}' with new status '{}'", generationId, updateData.getStatus().name());

        GenerationStatus newStatus = GenerationStatus.fromName(updateData.getStatus().name());
        record.setStatus(newStatus);
        record.setUpdated(Instant.now()); // The listener is responsible for the timestamp

        // Only process result-related fields if a result code is provided.
        if (updateData.getResultCode() != null) {
            // Safely convert the integer code to the GenerationResult enum
            Optional<GenerationResult> result = GenerationResult.fromCode(updateData.getResultCode());

            if (result.isPresent()) {
                record.setResult(result.get());
            } else {
                log.warn("Received an unknown GenerationResult code '{}' for generation ID: {}",
                        updateData.getResultCode(), generationId);
                // Optionally, you could map this to a default error enum, like ERR_GENERAL
                record.setResult(GenerationResult.ERR_GENERAL);
            }
        }

        // Update reason and SBOM URLs if they are provided in the event
        if (updateData.getReason() != null) {
            record.setReason(updateData.getReason());
        }

        if (updateData.getSbomUrls() != null && !updateData.getSbomUrls().isEmpty()) {
            record.setSbomUrls(updateData.getSbomUrls());
        }

        // 4. Set the 'finished' timestamp if the status is a final one.
        if (newStatus.isFinal()) {
            record.setFinished(Instant.now());
        }

        // 5. Persist the updated record to the database.
        generationRepository.update(record);
        log.info("Successfully updated GenerationRecord '{}'", generationId);
    }
}
