package org.jboss.sbomer.dispatcher.core.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.jboss.sbomer.dispatcher.core.port.api.GenerationDispatcher;
import org.jboss.sbomer.dispatcher.core.port.spi.GenerationEventPublisher;
import org.jboss.sbomer.events.kafka.model.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.jboss.sbomer.dispatcher.core.ApplicationConstants.COMPONENT_NAME;

@ApplicationScoped
@Slf4j
public class GenerationDispatcherService implements GenerationDispatcher {

    GenerationEventPublisher generationEventPublisher;

    @Inject
    public GenerationDispatcherService(GenerationEventPublisher generationEventPublisher) {
        this.generationEventPublisher = generationEventPublisher;
    }

    @Override
    public void dispatch(RequestDataSpec requestData) {
        log.info("Dispatching {} individual generation requests.", requestData.getGenerationRequests().size());

        // For each request in the batch...
        for (var originalRequest : requestData.getGenerationRequests()) {

            // --- THIS IS YOUR CORE BUSINESS LOGIC ---
            // Build the recipe based on the target type.
            RecipeSpec recipe = buildRecipeFor(originalRequest.getTarget());

            // Create the new, enriched event payload
            GenerationDataSpec generationData = GenerationDataSpec.newBuilder()
                    .setRequestId(requestData.getRequestId())
                    .setGenerationRequest(originalRequest)
                    .setRecipe(recipe)
                    .build();

            // Build the final event with a new context
            GenerationCreated event = GenerationCreated.newBuilder()
                    .setContext(createNewContext()) // Helper to create new EventId, Timestamp, etc.
                    .setGenerationData(generationData)
                    .build();

            // Send the new event to the output topic
            generationEventPublisher.publish(event);
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
                    .setName("syft")
                    .setVersion("1.5.0")
                    .build();
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
}
