package org.jboss.sbomer.dispatcher.core.port.spi;

import org.jboss.sbomer.dispatcher.core.domain.dto.GenerationRecord;
import org.jboss.sbomer.dispatcher.core.domain.dto.RequestRecord;

import java.util.List;

/**
 * The driven port for persisting and retrieving the state of requests and generations.
 * This is the contract for the "filing cabinet" of the Status Keeper.
 */
public interface StatusRepository {

    /**
     * Saves a new Request and all of its associated Generation records for the first time.
     * This is used when a 'requests.created' event is processed.
     *
     * @param request The new RequestRecord to persist.
     */
    void save(RequestRecord request);

    /**
     * Finds a single Generation record by its unique ID.
     * This is used to find the "square" on the bingo card that needs to be updated.
     *
     * @param generationId The ID of the generation to find.
     * @return The found GenerationRecord, or null if it doesn't exist.
     */
    GenerationRecord findGenerationById(String generationId);

    /**
     * Finds a single Request record by its unique ID, including all its children.
     * This is useful for the REST API and for fetching the full context during an update.
     *
     * @param requestId The ID of the request to find.
     * @return The found RequestRecord, or null if it doesn't exist.
     */
    RequestRecord findRequestById(String requestId);

    /**
     * Saves changes made to a Request and its associated Generations.
     * This is the main method used to update the state of the "bingo card".
     *
     * @param request The updated RequestRecord object to persist.
     */
    void update(RequestRecord request);

    /**
     * Finds all requests, typically for a top-level API view.
     *
     * @return A list of all RequestRecords.
     */
    List<RequestRecord> findAllRequests();

}
