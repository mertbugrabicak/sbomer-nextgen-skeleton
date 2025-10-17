package org.jboss.sbomer.dispatcher.core.port.api;

import org.jboss.sbomer.events.kafka.model.RequestDataSpec;
import org.jboss.sbomer.events.kafka.model.RequestsCreated;

import java.util.List;

/**
 * TODO
 */
public interface GenerationDispatcher {
    /**
     * TODO
     */
    public default void dispatch(RequestDataSpec requestData) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
