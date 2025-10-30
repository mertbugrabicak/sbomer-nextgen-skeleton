package org.jboss.sbomer.generator.adapter.out;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
// Import all the newly generated Avro model classes

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.sbomer.events.kafka.generator.GenerationFinal;
import org.jboss.sbomer.events.kafka.generator.GenerationFinalData;
import org.jboss.sbomer.events.kafka.common.ContextSpec;
import org.jboss.sbomer.events.kafka.dispatcher.GenerationCreated;
import org.jboss.sbomer.events.kafka.generator.GenerationFinished;
import org.jboss.sbomer.events.kafka.generator.GenerationFinishedData;
import org.jboss.sbomer.generator.core.domain.dto.GenerationRecord;
import org.jboss.sbomer.generator.core.port.spi.FailureNotifier;
import org.jboss.sbomer.generator.core.port.spi.GenerationResultPublisher;
import org.jboss.sbomer.generator.core.utility.FailureUtility;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class KafkaGenerationResultPublisher implements GenerationResultPublisher {

    @Inject @Channel("generation-finished") Emitter<GenerationFinished> finishedEmitter;
    @Inject @Channel("generation-final") Emitter<GenerationFinal> finalEmitter;

    @Inject
    FailureNotifier failureNotifier;

    // Use Avro's specific reader for reliable deserialization
    private final DatumReader<GenerationCreated> reader = new SpecificDatumReader<>(GenerationCreated.class);

    @Override
    public void publishSuccess(GenerationRecord record) {
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

        // The rest of your logic is now safe to run
        if (originalEvent.getGenerationData().getRecipe().getEnhancers().isEmpty()) {
            publishFinalEvent(originalEvent, record);
        } else {
            publishFinishedEvent(originalEvent, record);
        }
    }

    // --- Private Helper Methods (No changes needed here) ---

    private void publishFinalEvent(GenerationCreated originalEvent, GenerationRecord record) {
        GenerationFinalData finalData = new GenerationFinalData();
        finalData.setSbomUrls(record.getSbomUrls());
        finalData.setOriginalEvent(originalEvent);

        GenerationFinal finalEvent = new GenerationFinal();
        finalEvent.setContext(createNewContext());
        finalEvent.setData(finalData);

        finalEmitter.send(finalEvent);
        log.debug("Published 'generation.final' event for ID '{}'", record.getId());
        log.debug("Published 'generation.final' event '{}'", finalEvent.toString());
    }

    private void publishFinishedEvent(GenerationCreated originalEvent, GenerationRecord record) {
        GenerationFinishedData finishedData = new GenerationFinishedData();
        finishedData.setBaseSbomUrls(record.getSbomUrls());
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