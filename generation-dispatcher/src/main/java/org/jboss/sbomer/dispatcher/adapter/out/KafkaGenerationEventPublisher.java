package org.jboss.sbomer.adapter.et.adapter.out;

import java.util.List;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
// Import all the newly generated Avro model classes

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.jboss.sbomer.dispatcher.core.port.spi.GenerationEventPublisher;
import org.jboss.sbomer.events.kafka.dispatcher.GenerationCreated;

@ApplicationScoped
@Slf4j
public class KafkaGenerationEventPublisher implements GenerationEventPublisher {

    @Channel("generation-created")
    Emitter<GenerationCreated> emitter;

    @Override
    public void publish(GenerationCreated generationCreated) {
        emitter.send(generationCreated);
        log.debug("Sent generation event {}", generationCreated.toString());
    }
}