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
     * A single consumer method that listens to all relevant topics defined
     * in the 'sbomer-events' channel. It routes incoming events to the
     * appropriate business logic based on their type.
     */
    @Incoming("sbomer-events")
    public void handleEvent(Object event) {
        log.debug("Received a new system event of type '{}'", event.getClass().getSimpleName());

        // Use pattern matching with instanceof to route the event
        if (event instanceof RequestsCreated rc) {
            log.info("Processing 'requests.created' event with RequestId '{}'", rc.getRequestData().getRequestId());
            eventProcessor.processNewRequest(rc);
        } else if (event instanceof GenerationFinished gf) {
            String id = gf.getData().getOriginalEvent().getGenerationData().getGenerationRequest().getGenerationId();
            log.info("Processing 'generation.finished' for Generation ID '{}'", id);
            eventProcessor.updateGenerationStatus(gf);
        } else if (event instanceof GenerationFinal gfn) {
            String id = gfn.getData().getOriginalEvent().getGenerationData().getGenerationRequest().getGenerationId();
            log.info("Processing 'generation.final' for Generation ID '{}'", id);
            eventProcessor.updateGenerationStatus(gfn);
        } else if (event instanceof EnhancementFinished ef) {
            String id = ef.getData().getOriginalEvent().getGenerationData().getGenerationRequest().getGenerationId();
            log.info("Processing 'enhancement.finished' for Generation ID '{}'", id);
            eventProcessor.updateGenerationStatus(ef);
        } else if (event instanceof EnhancementFinal efn) {
            String id = efn.getData().getOriginalEvent().getGenerationData().getGenerationRequest().getGenerationId();
            log.info("Processing 'enhancement.final' for Generation ID '{}'", id);
            eventProcessor.updateGenerationStatus(efn);
        } else if (event instanceof ProcessingFailed pf) {
            String id = findGenerationIdInSourceEvent(pf.getErrorData().getSourceEvent());
            log.info("Processing 'processing.failed' for Generation ID '{}'", id);
            eventProcessor.updateGenerationStatus(pf);
        } else {
            log.warn("Received an unknown event type that will be ignored: {}", event.getClass().getName());
        }
    }

    /**
     * Helper to safely extract the GenerationId from the Avro union type in a ProcessingFailed event.
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
        return null;
    }
}
