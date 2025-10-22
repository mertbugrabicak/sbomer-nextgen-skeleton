package org.jboss.sbomer.generator.adapter.in;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.sbomer.events.kafka.dispatcher.GenerationCreated;
import org.jboss.sbomer.generator.core.port.api.GenerationInitiator;

@ApplicationScoped
public class KafkaGenerationInitiator {

    @Inject
    GenerationInitiator generationInitiator;

    @Incoming("generation-created")
    public void initiateGeneration(GenerationCreated event) {
        generationInitiator.initiateGeneration(event);
    }
}
