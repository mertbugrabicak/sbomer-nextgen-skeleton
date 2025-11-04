package org.jboss.sbomer.sbom.service.adapter.out.generation;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
// Import all the newly generated Avro model classes

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.jboss.sbomer.events.kafka.generation.GenerationCreated;
import org.jboss.sbomer.sbom.service.core.port.spi.generation.GenerationScheduler;

@ApplicationScoped
@Slf4j
public class KafkaGenerationScheduler implements GenerationScheduler {

    @Channel("generation-created")
    Emitter<GenerationCreated> emitter;

    @Override
    public void schedule(GenerationCreated generationCreated) {
        emitter.send(generationCreated);
        log.debug("Sent generation event {}", generationCreated.toString());
    }
}