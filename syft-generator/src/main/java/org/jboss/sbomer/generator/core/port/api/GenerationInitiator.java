package org.jboss.sbomer.generator.core.port.api;

import org.jboss.sbomer.events.kafka.dispatcher.GenerationCreated;

public interface GenerationInitiator {
    /**
     * Starts a new SBOM generation process based on an incoming event.
     */
    void initiateGeneration(GenerationCreated event);
}
