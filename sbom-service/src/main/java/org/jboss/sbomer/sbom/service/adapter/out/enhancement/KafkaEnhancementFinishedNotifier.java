package org.jboss.sbomer.sbom.service.adapter.out.enhancement;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.sbomer.events.kafka.common.ContextSpec;
import org.jboss.sbomer.events.kafka.enhancer.EnhancementFinished;
import org.jboss.sbomer.sbom.service.core.domain.dto.EnhancementRecord;
import org.jboss.sbomer.sbom.service.core.port.spi.enhancement.EnhancementFinishedNotifier;
import org.jboss.sbomer.sbom.service.core.port.spi.FailureNotifier;

import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class KafkaEnhancementFinishedNotifier implements EnhancementFinishedNotifier {

    @Inject @Channel("enhancement-finished")
    Emitter<EnhancementFinished> finishedEmitter;

    @Inject
    FailureNotifier failureNotifier;

    @Override
    public void notifyFinished(EnhancementRecord record) {
        log.info("Publishing success for Enhancement ID '{}'", record.getId());
    }

    private ContextSpec createNewContext() {
        ContextSpec context = new ContextSpec();
        context.setEventId(UUID.randomUUID().toString());
        context.setSource("sbomer-generator");
        context.setTimestamp(Instant.now());
        context.setSpecVersion("1.0");
        return context;
    }
}
