package org.jboss.sbomer.generator.core.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.jboss.sbomer.events.kafka.dispatcher.GenerationCreated;
import org.jboss.sbomer.generator.core.port.api.GenerationInitiator;
import org.jboss.sbomer.generator.core.port.spi.FailureNotifier;

@ApplicationScoped
@Slf4j
public class GenerationService implements GenerationInitiator {

    @Inject
    FailureNotifier failureNotifier;

    @Override
    public void initiateGeneration(GenerationCreated event) {
        // 1. Filter: Only process if the generator is "syft-generator"
        String generatorName = event.getGenerationData().getRecipe().getGenerator().getName();
        if (!"syft-generator".equalsIgnoreCase(generatorName)) {
            log.debug("Skipping event, generator is not 'syft-generator': {}", generatorName);
            return;
        }

        // --- This code is only reached if the generator matches ---

        String generationId = event.getGenerationData().getGenerationRequest().getGenerationId();
        log.info("Creating new TaskRun for Generation ID '{}'.", generationId);

        // ... rest of the logic to create the simulated TaskRun ...
    }



}
