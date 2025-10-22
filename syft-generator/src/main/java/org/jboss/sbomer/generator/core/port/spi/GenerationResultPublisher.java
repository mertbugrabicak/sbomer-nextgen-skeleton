package org.jboss.sbomer.generator.core.port.spi;


import org.jboss.sbomer.events.kafka.dispatcher.GenerationCreated;

import java.util.List;

/**
 * <p>
 * TODO
 * </p>
 * 
 * <p>
 * It is an SPI to notify
 * </p>
 * 
 */
public interface GenerationResultPublisher {
    public void publishSuccess(GenerationCreated originalEvent, List<String> sbomUrls);
}
