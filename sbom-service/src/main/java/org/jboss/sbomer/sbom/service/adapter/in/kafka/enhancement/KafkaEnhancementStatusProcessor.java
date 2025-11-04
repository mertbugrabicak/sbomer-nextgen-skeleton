package org.jboss.sbomer.sbom.service.adapter.in.kafka.enhancement;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.sbomer.events.kafka.enhancer.EnhancementUpdate;
import org.jboss.sbomer.sbom.service.core.port.api.enhancement.EnhancementStatusProcessor;

/**
 * Kafka event listener that processes enhancement status
 */
@ApplicationScoped
@Slf4j
public class KafkaEnhancementStatusProcessor {

    private EnhancementStatusProcessor enhancementStatusProcessor;

    @Inject
    KafkaEnhancementStatusProcessor(EnhancementStatusProcessor enhancementStatusProcessor) {
        this.enhancementStatusProcessor = enhancementStatusProcessor;
    }

    @Incoming("enhancement-update")
    public void processEnhancementStatusUpdatesFromKafka(EnhancementUpdate enhancementUpdate) {
        log.info("Received a generation update from Kafka for requestId '{}'", enhancementUpdate);
        enhancementStatusProcessor.processStatusUpdate(enhancementUpdate);
    }
}
