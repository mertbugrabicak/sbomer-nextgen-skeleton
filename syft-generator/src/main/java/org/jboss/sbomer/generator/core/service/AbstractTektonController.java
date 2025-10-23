package org.jboss.sbomer.generator.core.service;

import io.quarkus.scheduler.Scheduled;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.jboss.sbomer.generator.core.domain.dto.GenerationRecord;
import org.jboss.sbomer.generator.core.domain.enums.GenerationStatus;
import org.jboss.sbomer.generator.core.port.spi.StateRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
public abstract class AbstractTektonController {

    @Inject
    StateRepository stateRepository;

    // How long to wait before considering a task "finished".
    public static final int SIMULATED_TASK_DURATION_SECONDS = 10;


    public void reconcileLoop() {
        log.debug("Controller reconcile loop running...");

        // Find all active work.
        List<GenerationRecord> scheduled = stateRepository.findByStatus(GenerationStatus.SCHEDULED);
        List<GenerationRecord> generating = stateRepository.findByStatus(GenerationStatus.GENERATING);

        // Call the abstract reconcile method for each piece of work.
        scheduled.forEach(this::reconcile);
        generating.forEach(this::reconcile);
    }

    /**
     * The main reconciliation logic.
     */
    public void reconcile(GenerationRecord record) {
        switch (record.getStatus()) {
            case SCHEDULED:
                reconcileScheduled(record);
                break;
            case GENERATING:
                reconcileGenerating(record);
                break;
            default:
                // Do nothing for NEW, FINISHED, or FAILED states.
                break;
        }
    }

    // Abstract methods to be implemented by the concrete generator.
    protected abstract void reconcileScheduled(GenerationRecord record);
    protected abstract void reconcileGenerating(GenerationRecord record);

    /**
     * Simulates checking if a Tekton TaskRun is finished.
     */
    protected boolean isFinished(GenerationRecord record) {
        long timeElapsed = Duration.between(record.getUpdated(), Instant.now()).getSeconds();
        return timeElapsed > SIMULATED_TASK_DURATION_SECONDS;
    }

    /**
     * Simulates checking if a Tekton TaskRun was successful.
     * For this demo, we will always assume success.
     */
    protected boolean isSuccessful(GenerationRecord record) {
        return true;
    }
}
