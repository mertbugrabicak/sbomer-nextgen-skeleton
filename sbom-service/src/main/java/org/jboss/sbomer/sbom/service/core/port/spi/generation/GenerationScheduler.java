package org.jboss.sbomer.sbom.service.core.port.spi.generation;

import org.jboss.sbomer.events.kafka.generation.GenerationCreated;

/**
 * <p>
 * Primary interface for scheduling individual SBOM generations
 * </p>
 */
public interface GenerationScheduler {
    void schedule(GenerationCreated generationCreated);
}
