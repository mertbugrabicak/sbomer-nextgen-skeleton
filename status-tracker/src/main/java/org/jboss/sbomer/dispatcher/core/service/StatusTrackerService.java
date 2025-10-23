package org.jboss.sbomer.dispatcher.core.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.jboss.sbomer.dispatcher.core.port.api.SystemEventProcessor;
import org.jboss.sbomer.dispatcher.core.port.spi.StatusRepository;
import org.jboss.sbomer.events.kafka.enhancer.EnhancementFinal;
import org.jboss.sbomer.events.kafka.enhancer.EnhancementFinished;
import org.jboss.sbomer.events.kafka.error.ProcessingFailed;
import org.jboss.sbomer.events.kafka.generator.GenerationFinal;
import org.jboss.sbomer.events.kafka.generator.GenerationFinished;
import org.jboss.sbomer.events.kafka.handler.RequestsCreated;

@ApplicationScoped
@Slf4j
public class StatusTrackerService implements SystemEventProcessor {

    private StatusRepository statusRepository;

    public StatusTrackerService(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }


    @Override
    public void processNewRequest(RequestsCreated event) {

    }

    @Override
    public void updateGenerationStatus(GenerationFinished generationFinishedEvent) {

    }

    @Override
    public void updateGenerationStatus(GenerationFinal generationFinalEvent) {

    }

    @Override
    public void updateGenerationStatus(EnhancementFinished enhancementFinishedEvent) {

    }

    @Override
    public void updateGenerationStatus(EnhancementFinal enhancementFinalEvent) {

    }

    @Override
    public void updateGenerationStatus(ProcessingFailed processingFailedEvent) {

    }
}
