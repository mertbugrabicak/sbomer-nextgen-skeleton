package org.jboss.sbomer.generator.core.port.spi;


import org.jboss.sbomer.events.kafka.generator.GenerationUpdate;

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
public interface GenerationUpdateNotifier {
    /**
     * Publishes a status update for the SBOM generation
     */
    void notifyUpdate(GenerationUpdate generationUpdate);
}
