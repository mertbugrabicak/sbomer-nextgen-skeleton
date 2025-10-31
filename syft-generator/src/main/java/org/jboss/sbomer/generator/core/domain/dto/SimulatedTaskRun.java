package org.jboss.sbomer.generator.core.domain.dto;

import lombok.Data;
import org.jboss.sbomer.events.kafka.common.GenerationRequestSpec;
import org.jboss.sbomer.events.kafka.dispatcher.RecipeSpec;
import org.jboss.sbomer.events.kafka.generator.GenerationStatus;

import java.time.Instant;
import java.util.List;

/**
 * A DTO representing a simulated Tekton TaskRun.
 * This is NOT a database entity. It's an in-memory object
 * that lives in the InMemoryTaskRunStore to simulate the K8s API.
 */
@Data
public class SimulatedTaskRun {
    private String id; // This is the GenerationId
    private GenerationStatus status;
    private Instant createdTimestamp;
    private Instant lastUpdateTimestamp;
    private Instant completionTimestamp;

    // --- Results (from TaskRun.status.results) ---
    private Integer resultCode;
    private String reason;
    private List<String> sbomUrls;

    // --- Input Data (from original GenerationCreated event) ---
    private GenerationRequestSpec originalRequest;
    private RecipeSpec originalRecipe;
}