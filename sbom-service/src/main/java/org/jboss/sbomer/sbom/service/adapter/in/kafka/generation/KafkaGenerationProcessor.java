package org.jboss.sbomer.sbom.service.adapter.in.kafka.generation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.sbomer.sbom.service.core.port.api.generation.GenerationProcessor;
import org.jboss.sbomer.events.kafka.handler.RequestsCreated;

/**
 * Kafka event listener that processes generation requests
 */
@ApplicationScoped
@Slf4j
public class KafkaGenerationProcessor {

    private GenerationProcessor generationProcessor;

    @Inject
    KafkaGenerationProcessor(GenerationProcessor generationProcessor) {
        this.generationProcessor = generationProcessor;
    }

    @Incoming("requests-created")
    public void processGenerationsFromKafka(RequestsCreated requestsCreated) {
        log.info("Received requests created. Setting up and dispatching generators");
        generationProcessor.processGenerations(requestsCreated);
    }
}
