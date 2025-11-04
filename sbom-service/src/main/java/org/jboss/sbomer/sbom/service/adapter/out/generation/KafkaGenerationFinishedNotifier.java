package org.jboss.sbomer.sbom.service.adapter.out.generation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.sbomer.events.kafka.common.ContextSpec;
import org.jboss.sbomer.events.kafka.generation.GenerationCreated;
import org.jboss.sbomer.events.kafka.generator.GenerationFinished;
import org.jboss.sbomer.events.kafka.generator.GenerationFinishedData;
import org.jboss.sbomer.sbom.service.core.domain.dto.GenerationRecord;
import org.jboss.sbomer.sbom.service.core.port.spi.FailureNotifier;
import org.jboss.sbomer.sbom.service.core.port.spi.generation.GenerationFinishedNotifier;
import org.jboss.sbomer.sbom.service.core.utility.FailureUtility;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class KafkaGenerationFinishedNotifier implements GenerationFinishedNotifier {

    @Inject @Channel("generation-finished")
    Emitter<GenerationFinished> finishedEmitter;

    @Inject
    FailureNotifier failureNotifier;

    // Use Avro's specific reader for reliable deserialization
    private final DatumReader<GenerationCreated> reader = new SpecificDatumReader<>(GenerationCreated.class);

    @Override
    public void notifyFinished(GenerationRecord record) {
        log.info("Publishing success for Generation ID '{}'", record.getId());

        GenerationCreated originalEvent;
        try {
            Decoder decoder = DecoderFactory.get().jsonDecoder(GenerationCreated.getClassSchema(), record.getEvent());
            originalEvent = reader.read(null, decoder);
            // -------------------------------------------------------------------
        } catch (IOException e) {
            log.error("Critical error: Failed to deserialize original event from database for Generation ID '{}'. Halting publication.", record.getId(), e);
            failureNotifier.notify(FailureUtility.buildFailureSpecFromException(e), null);
            return;
        }
        publishFinishedEvent(originalEvent, record);
    }

    // --- Private Helper Methods ---

    private void publishFinishedEvent(GenerationCreated originalEvent, GenerationRecord record) {
        GenerationFinishedData finishedData = new GenerationFinishedData();
        finishedData.setBaseSbomUrls(record.getGenerationSbomUrls());
        finishedData.setOriginalEvent(originalEvent);

        GenerationFinished finishedEvent = new GenerationFinished();
        finishedEvent.setContext(createNewContext());
        finishedEvent.setData(finishedData);

        finishedEmitter.send(finishedEvent);
        log.debug("Published 'generation.finished' event for ID '{}'", record.getId());
        log.debug("Published 'generation.finished' event '{}'", finishedEvent.toString());
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
