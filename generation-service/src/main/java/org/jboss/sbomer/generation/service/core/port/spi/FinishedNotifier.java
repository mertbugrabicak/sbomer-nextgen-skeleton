package org.jboss.sbomer.generation.service.core.port.spi;

import org.jboss.sbomer.events.kafka.generator.GenerationFinished;
import org.jboss.sbomer.generation.service.core.domain.dto.GenerationRecord;

/**
 * <p>
 * To notify an SBOM generation has finished
 * </p>
 */
public interface FinishedNotifier {

    /**
     * Notify that a generation has been finished
     * @param generationRecord
     */
    void notifyFinished(GenerationRecord generationRecord);

}
