package org.jboss.sbomer.adapter.et.core.port.spi;

import java.util.List;

import org.jboss.sbomer.adapter.et.core.domain.generation.GenerationRequest;

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
public interface GenerationRequestService {
    public void requestGenerations(List<GenerationRequest> generationRequests, List<String> publishers);
}
