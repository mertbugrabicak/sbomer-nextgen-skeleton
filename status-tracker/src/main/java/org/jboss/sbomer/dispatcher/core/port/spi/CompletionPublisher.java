package org.jboss.sbomer.dispatcher.core.port.spi;

import org.jboss.sbomer.dispatcher.core.domain.dto.RequestRecord;
import org.jboss.sbomer.events.kafka.handler.RequestsCreated;

public interface CompletionPublisher {
    void publish(RequestRecord finalRequestRecord);
}
