package org.jboss.sbomer.generator.core.port.spi;

import org.jboss.sbomer.generator.core.domain.dto.GenerationRecord;
import org.jboss.sbomer.generator.core.domain.enums.GenerationStatus;

import java.util.List;

public interface StateRepository {
    /**
     * Saves a GenerationRecord.
     */
    void save(GenerationRecord record);
    /**
     * Finds a GenerationRecord by its unique ID.
     */
    GenerationRecord findById(String generationId);

    /**
     * Finds a list of GenerationRecords with a specific status.
     * This is used by the scheduler to find new work.
     */
    List<GenerationRecord> findByStatus(GenerationStatus status);

    /**
     * Saves or updates a GenerationRecord in the database.
     */
    void update(GenerationRecord record);
}
