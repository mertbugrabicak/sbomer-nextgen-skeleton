package org.jboss.sbomer.adapter.et.core.port.api;

import java.util.List;

import org.jboss.sbomer.adapter.et.core.domain.generation.GenerationRequest;

/**
 * <p>
 * Primary interface representing operations related to handling advisories. All
 * operations in this interface are triggers (entry points) to the system
 * </p>
 * 
 * <p>
 * Implementations of this interface should provide the logic for processing
 * advisories and generating requests based on them.
 * </p>
 */
public interface AdvisoryHandler {
    /**
     * Request a generation for the given advisory identifier.
     * 
     * @param advisoryId Being the numerical identifier of the advisory in Errata
     *                   Tool
     * @return A {@link GenerationRequest} instance representing the generation
     *         request
     */
    public default List<GenerationRequest> requestGenerations(String advisoryId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
