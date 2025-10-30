package org.jboss.sbomer.adapter.et.adapter.out;

import java.time.Instant; // Required for the timestamp
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.sbomer.adapter.et.core.ApplicationConstants;
import org.jboss.sbomer.adapter.et.core.domain.generation.GenerationRequest;
import org.jboss.sbomer.adapter.et.core.port.spi.GenerationRequestService;
// Import all the newly generated Avro model classes
import org.jboss.sbomer.events.kafka.common.ContextSpec;
import org.jboss.sbomer.events.kafka.common.GenerationRequestSpec;
import org.jboss.sbomer.events.kafka.common.PublisherSpec;
import org.jboss.sbomer.events.kafka.handler.RequestsCreated;
import org.jboss.sbomer.events.kafka.handler.RequestDataSpec;
import org.jboss.sbomer.events.kafka.common.TargetSpec;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class KafkaGenerationRequester implements GenerationRequestService {

    @Channel("requests-created")
    Emitter<RequestsCreated> emitter;

    @Override
    public void requestGenerations(List<GenerationRequest> generationRequests, List<String> publisherNames) {

        // 1. Build the list of PublisherSpec objects for the Data payload
        List<PublisherSpec> publishers = publisherNames.stream()
                .map(name -> PublisherSpec.newBuilder()
                        .setName(name)
                        .setOptions(Collections.emptyMap())
                        .build())
                .collect(Collectors.toList());

        // 2. Build the list of GenerationRequestSpec objects for the Data payload
        List<GenerationRequestSpec> kafkaRequests = generationRequests.stream()
                .map(r -> GenerationRequestSpec.newBuilder()
                        .setGenerationId(UUID.randomUUID().toString())
                        .setTarget(TargetSpec.newBuilder()
                                .setIdentifier(r.target().identifier())
                                .setType(r.target().type())
                                .build())
                        .build())
                .collect(Collectors.toList());

        // 3. Build the inner "Data" object (the payload)
        RequestDataSpec data = RequestDataSpec.newBuilder()
                .setRequestId(UUID.randomUUID().toString())
                .setPublishers(publishers)
                .setGenerationRequests(kafkaRequests)
                .build();

        // 4. Build the inner "Context" object (the envelope)
        ContextSpec context = ContextSpec.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setSource(ApplicationConstants.HANDLER_TYPE)
                .setTimestamp(Instant.now())
                .setSpecVersion("1.0")
                .build();

        // 5. Build the final, top-level event by combining the context and data
        RequestsCreated event = RequestsCreated.newBuilder()
                .setContext(context)
                .setRequestData(data)
                .build();

        emitter.send(event);
        log.info("Sent '{}' event with EventId {} to Kafka", event.getClass().getSimpleName(), event.getContext().getEventId());
        // log.debug("Event: {}", event);
    }
}