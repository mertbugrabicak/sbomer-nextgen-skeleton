package org.jboss.sbomer.adapter.et.core.port.spi;

import java.util.List;

import org.jboss.sbomer.adapter.et.core.domain.generation.GenerationRequest;

/**
 * <p>
 * Primary interface representing operations related to requesting generations
 * within the system.
 * </p>
 * 
 * <p>
 * It is an SPI to communicate with the GEneration service.
 * </p>
 * 
 */
public interface GenerationService {
    public void requestGenerations(List<GenerationRequest> generationRequests);
}
