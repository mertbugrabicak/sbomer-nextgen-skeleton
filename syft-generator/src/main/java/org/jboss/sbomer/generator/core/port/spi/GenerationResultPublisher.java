package org.jboss.sbomer.generator.core.port.spi;


import org.jboss.sbomer.generator.core.domain.dto.GenerationRecord;

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
    /**
     * Publishes the final success event(s) for a completed generation.
     */
    void publishSuccess(GenerationRecord record);
}
