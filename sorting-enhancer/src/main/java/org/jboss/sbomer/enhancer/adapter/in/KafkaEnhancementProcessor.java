package org.jboss.sbomer.enhancer.adapter.in;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.sbomer.enhancer.core.service.EnhancementService;
import org.jboss.sbomer.events.kafka.enhancer.EnhancementFinished;
import org.jboss.sbomer.events.kafka.generator.GenerationFinished;

@ApplicationScoped
public class KafkaEnhancementProcessor {

    @Inject
    EnhancementService enhancementService;

    @Incoming("generation-finished")
    public void handleGenerationFinished(GenerationFinished event) {
        enhancementService.enhance(event);
    }

    @Incoming("enhancement-finished-in")
    public void handleEnhancementFinished(EnhancementFinished event) {
        enhancementService.enhance(event);
    }
}
