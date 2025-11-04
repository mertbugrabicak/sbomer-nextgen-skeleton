package org.jboss.sbomer.sbom.service.core.port.api.enhancement;

import org.jboss.sbomer.events.kafka.enhancer.EnhancementFinished;
import org.jboss.sbomer.events.kafka.generator.GenerationFinished;

/**
 * <p>
 * Primary interface for processing individual SBOM enhancements.
 * Reacts to finished generations or enhancements
 * </p>
 */
public interface EnhancementProcessor {
    // Process next enhancement from a finished generation if exists
    void process(GenerationFinished generationFinished);
    // Process next enhancement in the chain if exists
    void process(EnhancementFinished enhancementFinished);
}
