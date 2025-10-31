package org.jboss.sbomer.generator.adapter.in;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.sbomer.events.kafka.dispatcher.GenerationCreated;
import org.jboss.sbomer.generator.core.port.api.GenerationInitiator;

@ApplicationScoped
@Slf4j
public class KafkaGenerationInitiator {

    @Inject
    GenerationInitiator generationInitiator;

    @Incoming("generation-created")
    public void initiateGeneration(GenerationCreated event) {
        if (event == null || event.getGenerationData() == null) {
            log.warn("Received null or empty GenerationCreated event, skipping.");
            return;
        }
        log.info("Received GenerationCreated event for ID: {}", event.getGenerationData().getGenerationRequest().getGenerationId());
        generationInitiator.initiateGeneration(event);
    }
}
