package org.jboss.sbomer.sbom.service.adapter.in.kafka.generation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.sbomer.events.kafka.generator.GenerationUpdate;
import org.jboss.sbomer.sbom.service.core.port.api.generation.GenerationStatusProcessor;

/**
 * Kafka event listener that processes generation status
 */
@ApplicationScoped
@Slf4j
public class KafkaGenerationStatusProcessor {

    private GenerationStatusProcessor generationStatusProcessor;

    @Inject
    KafkaGenerationStatusProcessor(GenerationStatusProcessor generationStatusProcessor) {
        this.generationStatusProcessor = generationStatusProcessor;
    }

    @Incoming("generation-update")
    public void processGenerationStatusUpdatesFromKafka(GenerationUpdate generationUpdate) {
        log.info("Received a generation update from Kafka for requestId '{}'", generationUpdate);
        generationStatusProcessor.processStatusUpdate(generationUpdate);
    }
}
