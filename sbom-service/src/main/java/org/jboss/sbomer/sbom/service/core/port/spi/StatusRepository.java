package org.jboss.sbomer.sbom.service.core.port.spi;

import org.jboss.sbomer.sbom.service.core.domain.dto.EnhancementRecord;
import org.jboss.sbomer.sbom.service.core.domain.dto.GenerationRecord;
import org.jboss.sbomer.sbom.service.core.domain.enums.EnhancementStatus;
import org.jboss.sbomer.sbom.service.core.domain.enums.GenerationStatus;

import java.util.List;

/**
 * <p>
 * To store and fetch the status of an SBOM generation or enhancement
 * </p>
 */
public interface StatusRepository {
    /**
     * Saves a GenerationRecord.
     */
    void saveGeneration(GenerationRecord record);
    /**
     * Finds a GenerationRecord by its unique ID.
     */
    GenerationRecord findGenerationById(String generationId);

    /**
     * Finds a list of GenerationRecords with a specific status.
     * This is used by the scheduler to find new work.
     */
    List<GenerationRecord> findByGenerationStatus(GenerationStatus status);

    /**
     * Saves or updates a GenerationRecord in the database.
     */
    void updateGeneration(GenerationRecord record);
    /**
     * Saves a GenerationRecord.
     */
    void saveEnhancement(EnhancementRecord record);
    /**
     * Finds a EnhancementRecord by its unique ID.
     */
    EnhancementRecord findEnhancementById(String enhancementId);

    /**
     * Finds a list of EnhancementRecords with a specific status.
     * This is used by the scheduler to find new work.
     */
    List<EnhancementRecord> findByEnhancementStatus(EnhancementStatus status);

    /**
     * Saves or updates a EnhancementRecord in the database.
     */
    void updateEnhancement(EnhancementRecord record);
}
