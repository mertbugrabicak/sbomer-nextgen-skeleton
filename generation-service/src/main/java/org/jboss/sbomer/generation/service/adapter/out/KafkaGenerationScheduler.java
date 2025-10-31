package org.jboss.sbomer.generation.service.adapter.out;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
// Import all the newly generated Avro model classes

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.jboss.sbomer.generation.service.core.port.spi.GenerationScheduler;
import org.jboss.sbomer.events.kafka.dispatcher.GenerationCreated;

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