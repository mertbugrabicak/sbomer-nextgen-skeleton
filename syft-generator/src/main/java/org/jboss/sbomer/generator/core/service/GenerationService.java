package org.jboss.sbomer.generator.core.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.jboss.sbomer.events.kafka.common.ContextSpec;
import org.jboss.sbomer.events.kafka.dispatcher.GenerationCreated;
import org.jboss.sbomer.generator.core.port.api.GenerationInitiator;
import org.jboss.sbomer.generator.core.port.spi.FailureNotifier;
import org.jboss.sbomer.generator.core.port.spi.GenerationResultPublisher;
import org.jboss.sbomer.generator.core.port.spi.StateRepository;

import java.time.Instant;
import java.util.UUID;

import static org.jboss.sbomer.generator.core.ApplicationConstants.COMPONENT_NAME;

@ApplicationScoped
@Slf4j
public class GenerationService implements GenerationInitiator {

    StateRepository stateRepository;
    GenerationResultPublisher generationResultPublisher;
    FailureNotifier failureNotifier;

    @Inject
    public GenerationService(StateRepository stateRepository, GenerationResultPublisher generationResultPublisher, FailureNotifier failureNotifier) {
        this.stateRepository = stateRepository;
        this.generationResultPublisher = generationResultPublisher;
        this.failureNotifier = failureNotifier;
    }

    private ContextSpec createNewContext() {
        ContextSpec context = ContextSpec.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setSource(COMPONENT_NAME)
                .setTimestamp(Instant.now())
                .setSpecVersion("1.0")
                .build();
        return context;
    }

    @Override
    public void initiateGeneration(GenerationCreated event) {
        // TODO save a generationrecord to db
    }

}
