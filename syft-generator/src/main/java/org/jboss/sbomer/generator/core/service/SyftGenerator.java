package org.jboss.sbomer.generator.core.service;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.jboss.sbomer.events.kafka.common.ContextSpec;
import org.jboss.sbomer.events.kafka.generator.GenerationStatus;
import org.jboss.sbomer.events.kafka.generator.GenerationUpdate;
import org.jboss.sbomer.events.kafka.generator.UpdateDataSpec;
import org.jboss.sbomer.generator.core.domain.dto.SimulatedTaskRun;
import org.jboss.sbomer.generator.core.port.spi.GenerationUpdateNotifier;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class SyftGenerator extends AbstractTektonController {

    @Inject
    GenerationUpdateNotifier resultPublisher;

    @Inject
    InMemoryTaskRunStore tektonApi; // Our simulated K8s/Tekton API

    @Scheduled(every = "7s", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void scheduleReconciliation() {
        super.reconcileLoop(); // Just logs

        // 1. "List active TaskRuns from cluster"
        List<SimulatedTaskRun> activeTaskRuns = tektonApi.findActiveTaskRuns();

        if (activeTaskRuns.isEmpty()) {
            log.debug("No active TaskRuns found. Sleeping.");
            return;
        }

        log.info("Found {} active TaskRun(s) to reconcile.", activeTaskRuns.size());

        // 2. Reconcile each one
        for (SimulatedTaskRun taskRun : activeTaskRuns) {
            reconcile(taskRun);
        }
    }

    /**
     * Handles TaskRuns that are in SCHEDULED state.
     * We will move them to GENERATING and notify Kafka.
     */
    @Override
    protected void reconcileScheduled(SimulatedTaskRun taskRun) {
        log.info("SyftGenerator: Reconciling SCHEDULED TaskRun '{}'.", taskRun.getId());

        // 1. Update the status of the "TaskRun" object
        taskRun.setStatus(GenerationStatus.GENERATING);
        taskRun.setLastUpdateTimestamp(Instant.now());

        // 2. "Patch the TaskRun resource in the cluster"
        tektonApi.update(taskRun);

        // 3. Send Kafka update to the external GenerationService
        log.info("TaskRun {} status changed to GENERATING, sending update.", taskRun.getId());
        resultPublisher.notifyUpdate(buildUpdateEvent(taskRun));
    }

    /**
     * Handles TaskRuns that are in GENERATING state.
     * We will check if they are finished and notify Kafka.
     */
    @Override
    protected void reconcileGenerating(SimulatedTaskRun taskRun) {
        log.debug("SyftGenerator: Reconciling GENERATING TaskRun '{}'.", taskRun.getId());

        // 1. "Poll TaskRun.status.conditions"
        if (!isFinished(taskRun)) {
            log.debug("TaskRun for ID '{}' is still running (simulated)...", taskRun.getId());
            return; // Not done yet, will check again on next loop
        }

        // 2. The TaskRun is finished, check for success
        if (isSuccessful(taskRun)) {
            log.info("TaskRun for ID '{}' FINISHED successfully (simulated).", taskRun.getId());
            taskRun.setStatus(GenerationStatus.FINISHED);
            // **FIXED:** Use hard-coded integer '0' for success
            taskRun.setResultCode(0);
            taskRun.setSbomUrls(List.of("s3://syft-bucket/sbom-" + taskRun.getId() + ".json"));
        } else {
            log.info("TaskRun for ID '{}' FAILED (simulated).", taskRun.getId());
            taskRun.setStatus(GenerationStatus.FAILED);
            // **FIXED:** Use a hard-coded integer for failure (e.g., 5)
            taskRun.setResultCode(5);
            taskRun.setReason("Simulated Tekton TaskRun failure.");
        }

        // 3. Update timestamps
        taskRun.setCompletionTimestamp(Instant.now());
        taskRun.setLastUpdateTimestamp(Instant.now());

        // 4. "Patch final status to TaskRun resource in cluster"
        tektonApi.update(taskRun);

        // 5. Send the final Kafka update to the external GenerationService
        log.info("TaskRun {} is final, sending update.", taskRun.getId());
        resultPublisher.notifyUpdate(buildUpdateEvent(taskRun));
    }

    /**
     * Helper to build the Kafka event from a TaskRun's current state.
     * **FIXED:** This version uses standard new() and setters, not builders.
     */
    private GenerationUpdate buildUpdateEvent(SimulatedTaskRun taskRun) {

        // 1. Create the inner UpdateDataSpec object
        UpdateDataSpec data = new UpdateDataSpec();
        data.setGenerationId(taskRun.getId());
        data.setStatus(taskRun.getStatus()); // Assumes this is the Avro enum

        if (taskRun.getResultCode() != null) {
            data.setResultCode(taskRun.getResultCode());
        }
        if (taskRun.getReason() != null) {
            data.setReason(taskRun.getReason());
        }
        if (taskRun.getSbomUrls() != null) {
            data.setSbomUrls(taskRun.getSbomUrls());
        }

        // 2. Build the ContextSpec object
        ContextSpec context = new ContextSpec();
        context.setEventId(UUID.randomUUID().toString());
        context.setSource("syft-generator"); // This component's name
        context.setSpecVersion("1.0");
        context.setTimestamp(Instant.now());

        // 3. Create the top-level GenerationUpdate event
        GenerationUpdate updateEvent = new GenerationUpdate();
        updateEvent.setContext(context);
        updateEvent.setUpdateData(data);

        return updateEvent;
    }
}
