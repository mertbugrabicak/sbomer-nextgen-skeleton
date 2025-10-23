package org.jboss.sbomer.dispatcher.adapter.in;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.sbomer.dispatcher.core.port.api.SystemEventProcessor;
import org.jboss.sbomer.events.kafka.dispatcher.GenerationCreated;
import org.jboss.sbomer.events.kafka.enhancer.EnhancementFinal;
import org.jboss.sbomer.events.kafka.enhancer.EnhancementFinished;
import org.jboss.sbomer.events.kafka.error.ProcessingFailed;
import org.jboss.sbomer.events.kafka.generator.GenerationFinal;
import org.jboss.sbomer.events.kafka.generator.GenerationFinished;
import org.jboss.sbomer.events.kafka.handler.RequestsCreated;

@ApplicationScoped
@Slf4j
public class KafkaSystemEventProcessor {

    @Inject
    SystemEventProcessor eventProcessor; // Inject the core logic port

    /**
     * Consumes the initial 'requests.created' event to start tracking a new batch.
     */
    @Incoming("requests-created")
    public void handleRequestsCreated(RequestsCreated event) {
        log.info("Received 'requests.created' event with RequestId '{}'", event.getRequestData().getRequestId());
        eventProcessor.processNewRequest(event);
    }

    /**
     * Consumes 'generation.final' events, marking a generation as COMPLETED.
     */
    @Incoming("generation-final")
    public void handleGenerationFinal(GenerationFinal generationFinalEvent) {
        String generationId = generationFinalEvent.getData().getOriginalEvent().getGenerationData().getGenerationRequest().getGenerationId();
        log.info("Received 'generation.final' for Generation ID '{}'", generationId);
        eventProcessor.updateGenerationStatus(generationFinalEvent);
    }

    /**
     * Consumes 'enhancement.final' events, marking a generation as COMPLETED.
     */
    @Incoming("enhancement-final")
    public void handleEnhancementFinal(EnhancementFinal enhancementFinalEvent) {
        String generationId = enhancementFinalEvent.getData().getOriginalEvent().getGenerationData().getGenerationRequest().getGenerationId();
        log.info("Received 'enhancement.final' for Generation ID '{}'", generationId);
        eventProcessor.updateGenerationStatus(enhancementFinalEvent);
    }

    /**
     * Consumes intermediate generation events to update the status to IN_PROGRESS.
     */
    @Incoming("generation-finished")
    public void handleIntermediateStatusForGeneration(GenerationFinished generationFinishedEvent) {
        String generationId = generationFinishedEvent.getData().getOriginalEvent().getGenerationData().getGenerationRequest().getGenerationId();
        log.info("Received intermediate 'generation.finished' for Generation ID '{}'", generationId);
        eventProcessor.updateGenerationStatus(generationFinishedEvent);
    }

    /**
     * Consumes intermediate enhancement events to update the status to IN_PROGRESS.
     */
    @Incoming("enhancement-finished")
    public void handleIntermediateStatus(EnhancementFinished enhancementFinishedEvent) {
        String generationId = enhancementFinishedEvent.getData().getOriginalEvent().getGenerationData().getGenerationRequest().getGenerationId();
        log.info("Received intermediate 'enhancement.finished' for Generation ID '{}'", generationId);
        eventProcessor.updateGenerationStatus(enhancementFinishedEvent);
    }

    /**
     * Consumes 'processing.failed' events to mark a generation as FAILED.
     */
    @Incoming("processing-failed")
    public void handleProcessingFailed(ProcessingFailed processingFailedEvent) {
        String generationId = findGenerationIdInSourceEvent(processingFailedEvent.getErrorData().getSourceEvent());

        if (generationId != null) {
            log.info("Received 'processing.failed' for Generation ID '{}'", generationId);
            String reason = processingFailedEvent.getErrorData().getFailure().getReason();
            eventProcessor.updateGenerationStatus(processingFailedEvent);
        } else {
            log.warn("Received a 'processing.failed' event with an unknown or unsupported source event type. Could not extract GenerationId.");
        }
    }

    /**
     * Helper to safely extract the GenerationId from the Avro union type.
     */
    private String findGenerationIdInSourceEvent(Object sourceEvent) {
        if (sourceEvent instanceof GenerationCreated gc) {
            return gc.getGenerationData().getGenerationRequest().getGenerationId();
        } else if (sourceEvent instanceof GenerationFinished gf) {
            return gf.getData().getOriginalEvent().getGenerationData().getGenerationRequest().getGenerationId();
        } else if (sourceEvent instanceof GenerationFinal gfn) {
            return gfn.getData().getOriginalEvent().getGenerationData().getGenerationRequest().getGenerationId();
        } else if (sourceEvent instanceof EnhancementFinished ef) {
            return ef.getData().getOriginalEvent().getGenerationData().getGenerationRequest().getGenerationId();
        } else if (sourceEvent instanceof EnhancementFinal efn) {
            return efn.getData().getOriginalEvent().getGenerationData().getGenerationRequest().getGenerationId();
        }
        // Add other event types here as needed.
        return null;
    }
}
