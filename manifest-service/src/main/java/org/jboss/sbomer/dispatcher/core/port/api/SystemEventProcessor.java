    package org.jboss.sbomer.dispatcher.core.port.api;

    import org.jboss.sbomer.events.kafka.enhancer.EnhancementFinal;
    import org.jboss.sbomer.events.kafka.enhancer.EnhancementFinished;
    import org.jboss.sbomer.events.kafka.error.ProcessingFailed;
    import org.jboss.sbomer.events.kafka.generator.GenerationFinal;
    import org.jboss.sbomer.events.kafka.generator.GenerationFinished;
    import org.jboss.sbomer.events.kafka.handler.RequestsCreated;

    public interface SystemEventProcessor {

        // lay out the tracking for the initial request that gets created
        void processNewRequest(RequestsCreated event);
        // update the generation status as new related messages come in
        void updateGenerationStatus(GenerationFinished generationFinishedEvent);
        void updateGenerationStatus(GenerationFinal generationFinalEvent);
        void updateGenerationStatus(EnhancementFinished enhancementFinishedEvent);
        void updateGenerationStatus(EnhancementFinal enhancementFinalEvent);
        void updateGenerationStatus(ProcessingFailed processingFailedEvent);
    }
