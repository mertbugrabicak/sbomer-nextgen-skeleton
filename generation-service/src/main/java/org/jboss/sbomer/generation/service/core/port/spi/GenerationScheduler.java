package org.jboss.sbomer.generation.service.core.port.spi;


import org.jboss.sbomer.events.kafka.dispatcher.GenerationCreated;

/**
 * <p>
 * Primary interface for scheduling individual SBOM generations
 * </p>
 */
public interface GenerationScheduler {
    void schedule(GenerationCreated generationCreated);
}
