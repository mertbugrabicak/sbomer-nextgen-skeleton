package org.jboss.sbomer.generator.core.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.sbomer.events.kafka.generator.GenerationStatus;
import org.jboss.sbomer.generator.core.domain.dto.SimulatedTaskRun;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * A MOCK of the Tekton/Kubernetes API Server.
 * This is NOT a database for the generator component. It's an in-memory
 * representation of the external cluster state.
 *
 * This singleton bean is injected into both GenerationService (to create)
 * and SyftGenerator (to read/reconcile).
 */
@ApplicationScoped
public class InMemoryTaskRunStore {

    // Simulates the cluster's store of TaskRun objects
    private final Map<String, SimulatedTaskRun> taskRunStore = new ConcurrentHashMap<>();

    /**
     * Simulates "tektonClient.taskRuns().create(taskRun)"
     */
    public void create(SimulatedTaskRun taskRun) {
        taskRunStore.put(taskRun.getId(), taskRun);
    }

    /**
     * Simulates "tektonClient.taskRuns().withName(id).patch(status)"
     */
    public void update(SimulatedTaskRun taskRun) {
        taskRunStore.put(taskRun.getId(), taskRun);
    }

    /**
     * Simulates "tektonClient.taskRuns().list(labels...)"
     * Finds TaskRuns that are still "active"
     */
    public List<SimulatedTaskRun> findActiveTaskRuns() {
        return taskRunStore.values().stream()
                .filter(run -> run.getStatus() == GenerationStatus.SCHEDULED || run.getStatus() == GenerationStatus.GENERATING)
                .collect(Collectors.toList());
    }
}
