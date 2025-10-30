package org.jboss.sbomer.enhancer.adapter.out;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.sbomer.enhancer.core.ApplicationConstants;
import org.jboss.sbomer.enhancer.core.port.spi.ResultPublisher;
import org.jboss.sbomer.events.kafka.common.ContextSpec;
import org.jboss.sbomer.events.kafka.common.EnhancerSpec;
import org.jboss.sbomer.events.kafka.dispatcher.GenerationCreated;
import org.jboss.sbomer.events.kafka.enhancer.EnhancementFinal;
import org.jboss.sbomer.events.kafka.enhancer.EnhancementFinalData;
import org.jboss.sbomer.events.kafka.enhancer.EnhancementFinished;
import org.jboss.sbomer.events.kafka.enhancer.EnhancementFinishedData;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.jboss.sbomer.enhancer.core.ApplicationConstants.COMPONENT_NAME;
import static org.jboss.sbomer.enhancer.core.ApplicationConstants.COMPONENT_VERSION;

@ApplicationScoped
@Slf4j
public class KafkaResultPublisher implements ResultPublisher {

    @Inject
    @Channel("enhancement-finished-out")
    Emitter<EnhancementFinished> finishedEmitter;

    @Inject
    @Channel("enhancement-final")
    Emitter<EnhancementFinal> finalEmitter;

    @Override
    public void publishFinished(GenerationCreated originalEvent, List<String> newSbomUrls, List<String> baseSbomUrls) {
        EnhancementFinishedData data = new EnhancementFinishedData();
        data.setBaseSbomUrls(baseSbomUrls);
        data.setLastEnhancedSbomUrls(newSbomUrls); // The URLs produced by this enhancer
        data.setLastEnhancement(EnhancerSpec.newBuilder().setName(COMPONENT_NAME).setVersion(COMPONENT_VERSION).build());
        data.setOriginalEvent(originalEvent);

        EnhancementFinished event = new EnhancementFinished();
        event.setContext(createNewContext());
        event.setData(data); // Set the populated Data object

        finishedEmitter.send(event);
        log.info("Published 'enhancement.finished' event for Generation ID '{}'", originalEvent.getGenerationData().getGenerationRequest().getGenerationId());
        log.debug("enhancement.finished event: {}", event.toString());
    }

    @Override
    public void publishFinal(GenerationCreated originalEvent, List<String> finalUrls) {
        EnhancementFinalData data = new EnhancementFinalData();
        data.setSbomUrls(finalUrls);
        data.setOriginalEvent(originalEvent);

        EnhancementFinal event = new EnhancementFinal();
        event.setContext(createNewContext());
        event.setData(data); // Set the populated Data object

        finalEmitter.send(event);
        log.info("Published 'enhancement.final' event for Generation ID '{}'", originalEvent.getGenerationData().getGenerationRequest().getGenerationId());
        log.debug("enhancement.final event: {}", event.toString());
    }

    private ContextSpec createNewContext() {
        ContextSpec context = new ContextSpec();
        context.setEventId(UUID.randomUUID().toString());
        context.setSource(COMPONENT_NAME);
        context.setTimestamp(Instant.now());
        context.setSpecVersion("1.0");
        return context;
    }
}
