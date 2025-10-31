package org.jboss.sbomer.generation.service.core.port.api;

import org.jboss.sbomer.events.kafka.dispatcher.GenerationCreated;
import org.jboss.sbomer.events.kafka.generator.GenerationUpdate;

/**
 * Endpoint to track and update the status of an ongoing generation
 */
public interface GenerationStatusProcessor {
    void processStatusUpdate(GenerationUpdate generationUpdate);
}
