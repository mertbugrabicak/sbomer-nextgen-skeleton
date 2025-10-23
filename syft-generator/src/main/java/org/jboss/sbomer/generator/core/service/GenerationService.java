package org.jboss.sbomer.generator.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.jboss.sbomer.events.kafka.dispatcher.GenerationCreated;
import org.jboss.sbomer.generator.core.domain.dto.GenerationRecord;
import org.jboss.sbomer.generator.core.domain.enums.GenerationStatus;
import org.jboss.sbomer.generator.core.port.api.GenerationInitiator;
import org.jboss.sbomer.generator.core.port.spi.FailureNotifier;
import org.jboss.sbomer.generator.core.port.spi.GenerationResultPublisher;
import org.jboss.sbomer.generator.core.port.spi.StateRepository;
import org.jboss.sbomer.generator.core.utility.FailureUtility;

import java.time.Instant;
import java.util.Collections;

@ApplicationScoped
@Slf4j
public class GenerationService implements GenerationInitiator {

    private final StateRepository stateRepository;
    private final GenerationResultPublisher generationResultPublisher;
    private final FailureNotifier failureNotifier;
    private final ObjectMapper objectMapper; // For JSON serialization

    @Inject
    public GenerationService(
            StateRepository stateRepository,
            GenerationResultPublisher generationResultPublisher,
            FailureNotifier failureNotifier,
            ObjectMapper objectMapper) {
        this.stateRepository = stateRepository;
        this.generationResultPublisher = generationResultPublisher;
        this.failureNotifier = failureNotifier;
        this.objectMapper = objectMapper;
    }

    @Override
    public void initiateGeneration(GenerationCreated event) {
        String generationId = event.getGenerationData().getGenerationRequest().getGenerationId();
        log.info("Creating new GenerationRecord for ID '{}'.", generationId);

        try {
            // 1. Create a new GenerationRecord entity.
            GenerationRecord record = new GenerationRecord();
            record.setId(generationId);
            record.setStatus(GenerationStatus.NEW);
            record.setCreated(Instant.now());
            record.setSbomUrls(Collections.emptyList()); // Initialize with an empty list.

            // 2. Serialize the full event object to a JSON string for storage.
            // This preserves the full context for the controller to use later.
            record.setEvent(objectMapper.writeValueAsString(event));

            // 3. Use the repository port to save the new record to the database.
            stateRepository.save(record);

            log.info("GenerationRecord for ID '{}' saved with status NEW. The scheduler will pick it up.", generationId);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize the GenerationCreated event for ID '{}' into JSON.", generationId, e);
            // This is a critical system failure. If we can't even save the request,
            // we should notify the failure system immediately.
            failureNotifier.notify(FailureUtility.buildFailureSpecFromException(e), event);
        }
    }
}
