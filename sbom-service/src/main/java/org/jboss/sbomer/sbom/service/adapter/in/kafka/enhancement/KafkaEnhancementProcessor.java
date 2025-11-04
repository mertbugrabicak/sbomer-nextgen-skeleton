package org.jboss.sbomer.sbom.service.adapter.in.kafka.enhancement;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.sbomer.events.kafka.enhancer.EnhancementFinished;
import org.jboss.sbomer.events.kafka.generator.GenerationFinished;
import org.jboss.sbomer.sbom.service.core.port.api.enhancement.EnhancementProcessor;

@ApplicationScoped
@Slf4j
public class KafkaEnhancementProcessor {

    private EnhancementProcessor enhancementProcessor;

    // Process the next enhancement for a generation if exists
    @Incoming("generation-finished")
    public void process(GenerationFinished generationFinished) {
        enhancementProcessor.process(generationFinished);
    }
    // Process the next enhancement in the enhancement queue if exists
    @Incoming("enhancement-finished")
    public void process(EnhancementFinished enhancementFinished) {
        enhancementProcessor.process(enhancementFinished);
    }
}