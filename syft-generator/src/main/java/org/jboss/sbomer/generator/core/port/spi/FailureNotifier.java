package org.jboss.sbomer.generator.core.port.spi;

import org.jboss.sbomer.events.kafka.common.FailureSpec;

public interface FailureNotifier {
    /**
     * Notifies an external system about a failure.
     * @param failure The standardized details of the failure.
     * @param sourceEvent The original event object that caused the failure.
     */
    void notify(FailureSpec failure, Object sourceEvent);
}
