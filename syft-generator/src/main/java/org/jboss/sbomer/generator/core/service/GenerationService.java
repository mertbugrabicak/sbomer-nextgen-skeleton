package org.jboss.sbomer.generator.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.avro.specific.SpecificDatumWriter;
import org.jboss.sbomer.events.kafka.dispatcher.GenerationCreated;
import org.jboss.sbomer.generator.core.domain.dto.GenerationRecord;
import org.jboss.sbomer.generator.core.domain.enums.GenerationStatus;
import org.jboss.sbomer.generator.core.port.api.GenerationInitiator;
import org.jboss.sbomer.generator.core.port.spi.FailureNotifier;
import org.jboss.sbomer.generator.core.port.spi.GenerationResultPublisher;
import org.jboss.sbomer.generator.core.port.spi.StateRepository;
import org.jboss.sbomer.generator.core.utility.FailureUtility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;

@ApplicationScoped
@Slf4j
public class GenerationService implements GenerationInitiator {

    @Inject
    StateRepository stateRepository;

    @Inject
    FailureNotifier failureNotifier;

    // Use Avro's DatumWriter for reliable serialization
    private final DatumWriter<GenerationCreated> writer = new SpecificDatumWriter<>(GenerationCreated.class);

    @Override
    public void initiateGeneration(GenerationCreated event) {
        String generationId = event.getGenerationData().getGenerationRequest().getGenerationId();
        log.info("Creating new GenerationRecord for ID '{}'.", generationId);

        try {
            GenerationRecord record = new GenerationRecord();
            record.setId(generationId);
            record.setStatus(GenerationStatus.NEW);
            record.setCreated(Instant.now());
            record.setUpdated(record.getCreated());
            record.setSbomUrls(Collections.emptyList());

            record.setEvent(serializeEventToJson(event));
            // --------------------------------------------------------

            stateRepository.save(record);
            log.info("GenerationRecord for ID '{}' saved with status NEW.", generationId);

        } catch (IOException e) {
            log.error("Failed to serialize the GenerationCreated event for ID '{}' into JSON.", generationId, e);
            failureNotifier.notify(FailureUtility.buildFailureSpecFromException(e), event);
        }
    }

    /**
     * Helper method to serialize an Avro object to a JSON string in the format
     * that Avro's JsonDecoder expects.
     */
    private String serializeEventToJson(GenerationCreated event) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Use pretty printing for readability in the database if desired
            JsonEncoder encoder = EncoderFactory.get().jsonEncoder(GenerationCreated.getClassSchema(), outputStream, true);
            writer.write(event, encoder);
            encoder.flush();
            return outputStream.toString(StandardCharsets.UTF_8);
        }
    }
}
