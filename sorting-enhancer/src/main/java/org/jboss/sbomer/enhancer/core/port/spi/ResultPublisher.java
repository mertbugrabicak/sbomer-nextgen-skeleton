package org.jboss.sbomer.enhancer.core.port.spi;

import org.jboss.sbomer.events.kafka.dispatcher.GenerationCreated;

import java.util.List;

public interface ResultPublisher {
    void publishFinished(GenerationCreated originalEvent, List<String> lastEnhancedSbomUrls);
    void publishFinal(GenerationCreated originalEvent, List<String> finalSbomUrls);
}
