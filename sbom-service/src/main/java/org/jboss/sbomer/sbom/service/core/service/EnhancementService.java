package org.jboss.sbomer.sbom.service.core.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.jboss.sbomer.events.kafka.common.*;
import org.jboss.sbomer.events.kafka.enhancer.EnhancementFinished;
import org.jboss.sbomer.events.kafka.enhancer.EnhancementUpdate;
import org.jboss.sbomer.events.kafka.generation.EnhancementCreated;
import org.jboss.sbomer.events.kafka.generation.GenerationCreated;
import org.jboss.sbomer.events.kafka.generator.GenerationFinished;
import org.jboss.sbomer.sbom.service.core.port.api.enhancement.EnhancementStatusProcessor;
import org.jboss.sbomer.sbom.service.core.port.api.enhancement.EnhancementProcessor;
import org.jboss.sbomer.sbom.service.core.port.spi.FailureNotifier;
import org.jboss.sbomer.sbom.service.core.port.spi.StatusRepository;
import org.jboss.sbomer.sbom.service.core.port.spi.enhancement.EnhancementScheduler;

import java.time.Instant;
import java.util.UUID;

import static org.jboss.sbomer.sbom.service.core.ApplicationConstants.COMPONENT_NAME;

@ApplicationScoped
@Slf4j
public class EnhancementService implements EnhancementStatusProcessor, EnhancementProcessor {

    EnhancementScheduler enhancementScheduler;
    StatusRepository statusRepository;
    FailureNotifier failureNotifier;

    @Inject
    public EnhancementService(EnhancementScheduler enhancementScheduler, StatusRepository statusRepository, FailureNotifier failureNotifier) {
        this.enhancementScheduler = enhancementScheduler;
        this.statusRepository = statusRepository;
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

    // process the incoming updates from the enhancers
    @Override
    public void processStatusUpdate(EnhancementUpdate enhancementUpdate) {
        // TODO send enhancement finished event from here
    }

    @Override
    public void process(GenerationFinished generationFinished) {
        // TODO create an enhnacementcreated event
        EnhancementCreated enhancementCreated = createNextEnhancement(generationFinished);
        if (enhancementCreated != null) {
            enhancementScheduler.schedule(enhancementCreated);
        }
    }

    @Override
    public void process(EnhancementFinished enhancementFinished) {
        // TODO create an enhnacementcreated event
        EnhancementCreated enhancementCreated = createNextEnhancement(enhancementFinished);
        if (enhancementCreated != null) {
            enhancementScheduler.schedule(enhancementCreated);
        }
    }

    private EnhancementCreated createNextEnhancement(GenerationFinished generationFinished) {
        // TODO create enhancement event for first enhancement on the list if exists
        return null;
    }

    private EnhancementCreated createNextEnhancement(EnhancementFinished enhancementFinished) {
        // TODO create enhancement event for next enhancement on the list if exists
        return null;
    }

}
