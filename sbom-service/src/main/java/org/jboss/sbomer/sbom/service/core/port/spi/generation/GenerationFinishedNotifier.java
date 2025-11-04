package org.jboss.sbomer.sbom.service.core.port.spi.generation;

import org.jboss.sbomer.sbom.service.core.domain.dto.GenerationRecord;

/**
 * <p>
 * To notify an SBOM generation has finished
 * </p>
 */
public interface GenerationFinishedNotifier {

    /**
     * Notify that a generation has been finished
     * @param generationRecord
     */
    void notifyFinished(GenerationRecord generationRecord);

}
