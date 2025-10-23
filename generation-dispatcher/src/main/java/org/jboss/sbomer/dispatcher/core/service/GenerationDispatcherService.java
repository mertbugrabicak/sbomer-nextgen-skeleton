package org.jboss.sbomer.dispatcher.core.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.jboss.sbomer.dispatcher.core.port.api.GenerationDispatcher;
import org.jboss.sbomer.dispatcher.core.port.spi.FailureNotifier;
import org.jboss.sbomer.dispatcher.core.port.spi.GenerationEventPublisher;
import org.jboss.sbomer.dispatcher.core.utility.FailureUtility;
import org.jboss.sbomer.events.kafka.common.*;
import org.jboss.sbomer.events.kafka.dispatcher.GenerationCreated;
import org.jboss.sbomer.events.kafka.dispatcher.GenerationDataSpec;
import org.jboss.sbomer.events.kafka.dispatcher.RecipeSpec;
import org.jboss.sbomer.events.kafka.handler.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.jboss.sbomer.dispatcher.core.ApplicationConstants.COMPONENT_NAME;

@ApplicationScoped
@Slf4j
public class GenerationDispatcherService implements GenerationDispatcher {

    GenerationEventPublisher generationEventPublisher;
    FailureNotifier failureNotifier;

    @Inject
    public GenerationDispatcherService(GenerationEventPublisher generationEventPublisher, FailureNotifier failureNotifier) {
        this.generationEventPublisher = generationEventPublisher;
        this.failureNotifier = failureNotifier;
    }

    @Override
    public void dispatch(RequestsCreated event) {
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
                generationEventPublisher.publish(generationEvent);
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
}
