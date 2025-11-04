package org.jboss.sbomer.sbom.service.core.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.jboss.sbomer.events.kafka.generation.RecipeSpec;
import org.jboss.sbomer.events.kafka.generator.GenerationUpdate;
import org.jboss.sbomer.sbom.service.core.port.api.generation.GenerationProcessor;
import org.jboss.sbomer.sbom.service.core.port.api.generation.GenerationStatusProcessor;
import org.jboss.sbomer.sbom.service.core.port.spi.FailureNotifier;
import org.jboss.sbomer.sbom.service.core.port.spi.StatusRepository;
import org.jboss.sbomer.sbom.service.core.port.spi.generation.GenerationScheduler;
import org.jboss.sbomer.events.kafka.common.*;
import org.jboss.sbomer.events.kafka.handler.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.jboss.sbomer.sbom.service.core.ApplicationConstants.COMPONENT_NAME;

@ApplicationScoped
@Slf4j
public class GenerationService implements GenerationProcessor, GenerationStatusProcessor {

    GenerationScheduler generationScheduler;
    StatusRepository statusRepository;
    FailureNotifier failureNotifier;

    @Inject
    public GenerationService(GenerationScheduler generationScheduler, StatusRepository statusRepository, FailureNotifier failureNotifier) {
        this.statusRepository = statusRepository;
        this.generationScheduler = generationScheduler;
        this.failureNotifier = failureNotifier;
    }

    // Create recipes for each generation requested from the source and schedule them to be generated
    @Override
    public void processGenerations(RequestsCreated event) {

    }

    // Process the incoming updates from the generators
    @Override
    public void processStatusUpdate(GenerationUpdate generationUpdate) {

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
