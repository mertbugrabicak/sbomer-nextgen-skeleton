package org.jboss.sbomer.enhancer.adapter.out;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.sbomer.enhancer.core.ApplicationConstants;
import org.jboss.sbomer.enhancer.core.port.spi.FailureNotifier;
import org.jboss.sbomer.events.kafka.common.ContextSpec;
import org.jboss.sbomer.events.kafka.common.FailureSpec;
import org.jboss.sbomer.events.kafka.error.ErrorDataSpec;
import org.jboss.sbomer.events.kafka.error.ProcessingFailed;

import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class KafkaFailureNotifier implements FailureNotifier {

    @Inject
    @Channel("sbomer-failures") // Links to the Kafka channel in application.properties
    Emitter<ProcessingFailed> emitter;

    @Override
    public void notify(FailureSpec failure, Object sourceEvent) {
        // 1. Create the top-level event object
        ProcessingFailed pf = new ProcessingFailed();

        // 2. Build and set the event context
        ContextSpec context = new ContextSpec();
        context.setEventId(UUID.randomUUID().toString());
        context.setSource(ApplicationConstants.COMPONENT_NAME); // Set the source to identify this component
        context.setSpecVersion("1.0");
        context.setTimestamp(Instant.now());
        pf.setContext(context);

        // 3. Build and set the nested error data payload
        ErrorDataSpec errorData = new ErrorDataSpec();
        errorData.setFailure(failure);
        errorData.setSourceEvent(sourceEvent); // Can be null, Avro union handles this
        pf.setErrorData(errorData);

        // 4. Log the action for observability
        String eventType = (sourceEvent != null) ? sourceEvent.getClass().getSimpleName() : "N/A (initial trigger)";
        log.info("Publishing a failure notification for event of type '{}'. Reason: {}", eventType, failure.getReason());

        // 5. Send the event to the Kafka topic
        emitter.send(pf);

        log.debug("Failure notification sent successfully to Kafka topic 'sbomer.errors'.");
    }
}
