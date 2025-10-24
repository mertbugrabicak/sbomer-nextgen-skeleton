package org.jboss.sbomer.dispatcher.core.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.jboss.sbomer.dispatcher.core.domain.dto.GenerationRecord;
import org.jboss.sbomer.dispatcher.core.domain.dto.ManifestRecord;
import org.jboss.sbomer.dispatcher.core.domain.dto.RequestRecord;
import org.jboss.sbomer.dispatcher.core.domain.dto.enums.GenerationStatus;
import org.jboss.sbomer.dispatcher.core.domain.dto.enums.RequestStatus;
import org.jboss.sbomer.dispatcher.core.port.api.SystemEventProcessor;
import org.jboss.sbomer.dispatcher.core.port.spi.CompletionPublisher;
import org.jboss.sbomer.dispatcher.core.port.spi.StatusRepository;
import org.jboss.sbomer.events.kafka.dispatcher.GenerationCreated;
import org.jboss.sbomer.events.kafka.enhancer.EnhancementFinal;
import org.jboss.sbomer.events.kafka.enhancer.EnhancementFinished;
import org.jboss.sbomer.events.kafka.error.ProcessingFailed;
import org.jboss.sbomer.events.kafka.generator.GenerationFinal;
import org.jboss.sbomer.events.kafka.generator.GenerationFinished;
import org.jboss.sbomer.events.kafka.handler.RequestsCreated;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Slf4j
public class StatusTrackerService implements SystemEventProcessor {

    @Inject
    StatusRepository repository;
    @Inject
    CompletionPublisher publisher;

    @Override
    @Transactional
    public void processNewRequest(RequestsCreated event) {
        log.info("Processing new request '{}' with {} generations.", event.getRequestData().getRequestId(), event.getRequestData().getGenerationRequests().size());

        RequestRecord newRequest = new RequestRecord();
        newRequest.setId(event.getRequestData().getRequestId());
        newRequest.setCreatedAt(Instant.now());
        newRequest.setStatus(RequestStatus.IN_PROGRESS);
        newRequest.setTotalGenerations(event.getRequestData().getGenerationRequests().size());
        newRequest.setCompletedGenerations(0);
        newRequest.setOriginalRequestEvent(event);

        List<GenerationRecord> generations = new ArrayList<>();
        for (var reqSpec : event.getRequestData().getGenerationRequests()) {
            GenerationRecord gen = new GenerationRecord();
            gen.setId(reqSpec.getGenerationId());
            gen.setRequestId(newRequest.getId());
            gen.setStatus(GenerationStatus.NEW);
            gen.setTargetType(reqSpec.getTarget().getType());
            gen.setTargetIdentifier(reqSpec.getTarget().getIdentifier());
            generations.add(gen);
        }
        newRequest.setGenerations(generations);

        repository.save(newRequest);
    }

    // --- Public methods that translate events into a standard update call ---

    @Override
    public void updateGenerationStatus(GenerationFinished event) {
        String id = event.getData().getOriginalEvent().getGenerationData().getGenerationRequest().getGenerationId();
        updateAndCheckCompletion(id, GenerationStatus.ENHANCING, null, "Enhancement started");
    }

    @Override
    public void updateGenerationStatus(GenerationFinal event) {
        String id = event.getData().getOriginalEvent().getGenerationData().getGenerationRequest().getGenerationId();
        updateAndCheckCompletion(id, GenerationStatus.COMPLETED, event.getData().getSbomUrls(), "Generation finished successfully");
    }

    @Override
    public void updateGenerationStatus(EnhancementFinished event) {
        String id = event.getData().getOriginalEvent().getGenerationData().getGenerationRequest().getGenerationId();
        updateAndCheckCompletion(id, GenerationStatus.ENHANCING, null, "Enhancement in progress");
    }

    @Override
    public void updateGenerationStatus(EnhancementFinal event) {
        String id = event.getData().getOriginalEvent().getGenerationData().getGenerationRequest().getGenerationId();
        updateAndCheckCompletion(id, GenerationStatus.COMPLETED, event.getData().getSbomUrls(), "Enhancement finished successfully");
    }

    @Override
    public void updateGenerationStatus(ProcessingFailed event) {
        String id = findGenerationIdInSourceEvent(event.getErrorData().getSourceEvent());
        String reason = event.getErrorData().getFailure().getReason();
        updateAndCheckCompletion(id, GenerationStatus.FAILED, null, reason);
    }

    // --- The Core "Bingo Card" Logic ---

    @Transactional
    public void updateAndCheckCompletion(String generationId, GenerationStatus newStatus, List<String> sbomUrls, String reason) {
        if (generationId == null) {
            log.warn("Received a status update with no GenerationId, cannot process.");
            return;
        }

        // 1. Find the individual generation to get its parent RequestId.
        GenerationRecord generation = repository.findGenerationById(generationId);
        if (generation == null) {
            log.warn("Received status update for an unknown Generation ID '{}'.", generationId);
            return;
        }

        // 2. Fetch the whole parent RequestRecord. This is our "unit of work".
        RequestRecord parentRequest = repository.findRequestById(generation.getRequestId());

        // Find the specific child record within the parent's list to update.
        GenerationRecord generationToUpdate = parentRequest.getGenerations().stream()
                .filter(g -> g.getId().equals(generationId))
                .findFirst()
                .orElse(null);

        if (generationToUpdate == null) {
            log.error("Data inconsistency: Generation ID '{}' not found within its parent Request ID '{}'.", generationId, parentRequest.getId());
            return;
        }

        // Idempotency check: avoid re-processing terminal states.
        if (generationToUpdate.getStatus() == GenerationStatus.COMPLETED || generationToUpdate.getStatus() == GenerationStatus.FAILED) {
            log.warn("Generation ID '{}' is already in state '{}'. Ignoring new status update.", generationId, generationToUpdate.getStatus());
            return;
        }

        // 3. Update the state of the child object.
        generationToUpdate.setStatus(newStatus);
        generationToUpdate.setReason(reason);
        generationToUpdate.setUpdatedAt(Instant.now());

        if (newStatus == GenerationStatus.COMPLETED && sbomUrls != null) {
            List<ManifestRecord> manifests = new ArrayList<>();
            for (String url : sbomUrls) {
                ManifestRecord manifest = new ManifestRecord();
                manifest.setUrl(url);
                manifests.add(manifest);
            }
            generationToUpdate.setManifests(manifests);
        }

        boolean isTerminalState = (newStatus == GenerationStatus.COMPLETED || newStatus == GenerationStatus.FAILED);

        // 4. Update the parent counter if necessary.
        if (isTerminalState) {
            parentRequest.setCompletedGenerations(parentRequest.getCompletedGenerations() + 1);
        }

        // 5. Save all changes by passing the modified parent object to the repository.
        repository.update(parentRequest);

        // 6. THE GATEKEEPER CHECK: Is the whole request done?
        if (isTerminalState && parentRequest.getCompletedGenerations() == parentRequest.getTotalGenerations()) {
            boolean anyFailed = parentRequest.getGenerations().stream()
                    .anyMatch(g -> g.getStatus() == GenerationStatus.FAILED);

            parentRequest.setStatus(anyFailed ? RequestStatus.FAILED : RequestStatus.COMPLETED);
            repository.update(parentRequest);

            // Announce that the entire request is finished!
            publisher.publish(parentRequest);
        }
    }

    private String findGenerationIdInSourceEvent(Object sourceEvent) {
        if (sourceEvent instanceof GenerationCreated gc) {
            return gc.getGenerationData().getGenerationRequest().getGenerationId();
        } else if (sourceEvent instanceof GenerationFinished gf) {
            return gf.getData().getOriginalEvent().getGenerationData().getGenerationRequest().getGenerationId();
        } // ... and so on for other event types.
        return null;
    }
}
