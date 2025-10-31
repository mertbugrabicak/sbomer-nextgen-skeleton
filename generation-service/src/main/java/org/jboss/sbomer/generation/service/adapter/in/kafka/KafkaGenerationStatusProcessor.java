package org.jboss.sbomer.generation.service.adapter.in.kafka;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.sbomer.events.kafka.generator.GenerationUpdate;
import org.jboss.sbomer.events.kafka.handler.RequestsCreated;
import org.jboss.sbomer.generation.service.core.port.api.GenerationProcessor;
import org.jboss.sbomer.generation.service.core.port.api.GenerationStatusProcessor;

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
