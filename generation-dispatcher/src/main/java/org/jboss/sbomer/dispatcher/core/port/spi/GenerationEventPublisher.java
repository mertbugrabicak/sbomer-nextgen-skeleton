package org.jboss.sbomer.dispatcher.core.port.spi;


import org.jboss.sbomer.events.kafka.dispatcher.GenerationCreated;

/**
 * <p>
 * Primary interface representing operations related to requesting a list generations and defining where they should be published
 * within the system.
 * </p>
 * 
 * <p>
 * It is an SPI to request SBOM generations and define where they should be published
 * </p>
 * 
 */
public interface GenerationEventPublisher {
    public void publish(GenerationCreated generationCreated);
}
