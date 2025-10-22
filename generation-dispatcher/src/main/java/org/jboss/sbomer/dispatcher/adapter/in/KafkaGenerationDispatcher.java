package org.jboss.sbomer.dispatcher.adapter.in;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.sbomer.dispatcher.core.port.api.GenerationDispatcher;
import org.jboss.sbomer.events.kafka.handler.RequestsCreated;

/**
 * TODO: Implement the event handling and dispatching of generations from requests.
 */
@ApplicationScoped
@Slf4j
public class KafkaGenerationDispatcher {

    private GenerationDispatcher generationDispatcher;

    @Inject
    KafkaGenerationDispatcher(GenerationDispatcher generationDispatcher) {
        this.generationDispatcher = generationDispatcher;
    }

    @Incoming("requests-created")
    public void dispatch(RequestsCreated requestsCreated) {
        log.info("Received requests created. Setting up and dispatching generators");
        generationDispatcher.dispatch(requestsCreated);
    }
}
