package org.jboss.sbomer.dispatcher.adapter.out;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.sbomer.dispatcher.core.domain.dto.GenerationRecord;
import org.jboss.sbomer.dispatcher.core.domain.dto.ManifestRecord;
import org.jboss.sbomer.dispatcher.core.domain.dto.RequestRecord;
import org.jboss.sbomer.dispatcher.core.port.spi.CompletionPublisher;
import org.jboss.sbomer.dispatcher.core.port.spi.FailureNotifier;
import org.jboss.sbomer.dispatcher.core.utility.FailureUtility;
import org.jboss.sbomer.events.kafka.common.ContextSpec;
import org.jboss.sbomer.events.kafka.common.PublisherSpec;
import org.jboss.sbomer.events.kafka.common.TargetSpec;
import org.jboss.sbomer.events.kafka.dispatcher.GenerationCreated;
import org.jboss.sbomer.events.kafka.handler.RequestsCreated;
import org.jboss.sbomer.events.kafka.status.CompletedGeneration;
import org.jboss.sbomer.events.kafka.status.RequestsFinished;
import org.jboss.sbomer.events.kafka.status.RequestsFinishedData;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
public class KafkaCompletionPublisher implements CompletionPublisher {

    @Inject
    @Channel("requests-finished")
    Emitter<RequestsFinished> emitter;

    @Inject
    FailureNotifier failureNotifier;

    @Override
    public void publish(RequestRecord finalRequestRecord) {
        log.info("Publishing completion for Request ID '{}'", finalRequestRecord.getId());

        try {
            // 1. Extract the Publishers list from the original event stored in the DTO.
            List<PublisherSpec> publishers = finalRequestRecord.getOriginalRequestEvent()
                    .getRequestData()
                    .getPublishers();

            // 2. Create the list of CompletedGeneration objects from the DTOs.
            List<CompletedGeneration> completedGenerations = new ArrayList<>();
            for (GenerationRecord genRecord : finalRequestRecord.getGenerations()) {

                // Reconstruct the TargetSpec from the denormalized fields.
                TargetSpec target = new TargetSpec();
                target.setType(genRecord.getTargetType());
                target.setIdentifier(genRecord.getTargetIdentifier());

                // Build the completed generation object for the event payload.
                CompletedGeneration completed = new CompletedGeneration();
                completed.setGenerationId(genRecord.getId());
                completed.setTarget(target);

                // Map ManifestRecord URLs to a simple list of strings.
                List<String> urls = genRecord.getManifests().stream()
                        .map(ManifestRecord::getUrl)
                        .collect(Collectors.toList());
                completed.setSbomUrls(urls);

                completedGenerations.add(completed);
            }

            // 3. Build the main event data payload.
            RequestsFinishedData data = new RequestsFinishedData();
            data.setRequestId(finalRequestRecord.getId());
            data.setPublishers(publishers);
            data.setCompletedGenerations(completedGenerations);

            // 4. Build the top-level event.
            RequestsFinished event = new RequestsFinished();
            event.setContext(createNewContext());
            event.setData(data);

            // 5. Send the event to Kafka.
            emitter.send(event);
            log.info("Successfully published 'requests.finished' event for Request ID '{}'", finalRequestRecord.getId());

        } catch (Exception e) {
            log.error("Critical error: Failed to build and publish 'requests.finished' event for Request ID '{}'", finalRequestRecord.getId(), e);
            // This is a system failure within the Status Keeper itself.
            failureNotifier.notify(FailureUtility.buildFailureSpecFromException(e), finalRequestRecord);
        }
    }

    private ContextSpec createNewContext() {
        ContextSpec context = new ContextSpec();
        context.setEventId(UUID.randomUUID().toString());
        context.setSource("status-keeper"); // Identify this component
        context.setTimestamp(Instant.now());
        context.setSpecVersion("1.0");
        return context;
    }
}
