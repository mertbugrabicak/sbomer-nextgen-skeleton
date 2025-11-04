package org.jboss.sbomer.sbom.service.core.port.spi.enhancement;

import org.jboss.sbomer.events.kafka.generation.EnhancementCreated;

/**
 * <p>
 * Primary interface for scheduling individual SBOM enhancements
 * </p>
 */
public interface EnhancementScheduler {
    void schedule(EnhancementCreated enhancementCreated);
}
