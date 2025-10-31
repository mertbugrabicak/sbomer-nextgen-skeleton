package org.jboss.sbomer.generator.adapter.out;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.sbomer.events.kafka.error.ProcessingFailed;
import org.jboss.sbomer.events.kafka.generator.GenerationUpdate;
import org.jboss.sbomer.generator.core.port.spi.GenerationUpdateNotifier;

@ApplicationScoped
@Slf4j
public class KafkaGenerationUpdateNotifier implements GenerationUpdateNotifier {

    @Inject
    @Channel("generation-update") // Links to the Kafka channel in application.properties
    Emitter<GenerationUpdate> emitter;

    @Override
    public void notifyUpdate(GenerationUpdate generationUpdate) {
        String id = generationUpdate.getUpdateData().getGenerationId();
        String status = generationUpdate.getUpdateData().getStatus().name();

        log.info("Publishing GenerationUpdate for ID '{}' with status '{}'", id, status);

        emitter.send(generationUpdate).toCompletableFuture()
                .exceptionally(ex -> {
                    log.error("Failed to send GenerationUpdate to Kafka for ID '{}'", id, ex);
                    return null;
                });
    }
}
