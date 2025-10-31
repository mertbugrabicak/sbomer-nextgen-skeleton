package org.jboss.sbomer.generator.core.service;

import lombok.extern.slf4j.Slf4j;
import org.jboss.sbomer.generator.core.domain.dto.SimulatedTaskRun;

import java.time.Duration;
import java.time.Instant;

@Slf4j
public abstract class AbstractTektonController {

    // How long to wait before considering a task "finished".
    public static final int SIMULATED_TASK_DURATION_SECONDS = 10;

    public void reconcileLoop() {
        log.debug("Controller reconcile loop running...");
    }

    /**
     * The main reconciliation logic.
     * Takes the DTO that represents the external TaskRun.
     */
    public void reconcile(SimulatedTaskRun taskRun) {
        switch (taskRun.getStatus()) {
            case SCHEDULED:
                reconcileScheduled(taskRun);
                break;
            case GENERATING:
                reconcileGenerating(taskRun);
                break;
            default:
                // Do nothing for NEW, FINISHED, or FAILED states.
                break;
        }
    }

    // Abstract methods to be implemented by the concrete generator.
    protected abstract void reconcileScheduled(SimulatedTaskRun taskRun);
    protected abstract void reconcileGenerating(SimulatedTaskRun taskRun);

    /**
     * Simulates checking if a Tekton TaskRun is finished.
     */
    protected boolean isFinished(SimulatedTaskRun taskRun) {
        long timeElapsed = Duration.between(taskRun.getLastUpdateTimestamp(), Instant.now()).getSeconds();
        return timeElapsed > SIMULATED_TASK_DURATION_SECONDS;
    }

    /**
     * Simulates checking if a Tekton TaskRun was successful.
     * For this demo, we will always assume success.
     */
    protected boolean isSuccessful(SimulatedTaskRun taskRun) {
        return true;
    }
}
