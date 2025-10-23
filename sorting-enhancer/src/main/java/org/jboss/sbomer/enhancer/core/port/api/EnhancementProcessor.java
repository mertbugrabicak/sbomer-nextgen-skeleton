package org.jboss.sbomer.enhancer.core.port.api;

import org.jboss.sbomer.events.kafka.enhancer.EnhancementFinished;
import org.jboss.sbomer.events.kafka.generator.GenerationFinished;

public interface EnhancementProcessor {
    void process(GenerationFinished event);
    void process(EnhancementFinished event);
}
