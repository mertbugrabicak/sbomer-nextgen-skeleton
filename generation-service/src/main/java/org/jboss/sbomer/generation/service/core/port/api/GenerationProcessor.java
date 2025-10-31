package org.jboss.sbomer.generation.service.core.port.api;

import org.jboss.sbomer.events.kafka.handler.RequestsCreated;

/**
 * API to process prepared generation requests coming from Handlers within SBOMer
 */
public interface GenerationProcessor {

    /**
     * Process a list of generations requested
     */
    default void processGenerations(RequestsCreated requestsCreated) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
