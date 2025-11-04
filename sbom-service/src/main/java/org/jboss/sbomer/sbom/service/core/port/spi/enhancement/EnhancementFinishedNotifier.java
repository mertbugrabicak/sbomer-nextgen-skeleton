package org.jboss.sbomer.sbom.service.core.port.spi.enhancement;

import org.jboss.sbomer.sbom.service.core.domain.dto.EnhancementRecord;

/**
 * <p>
 * To notify an SBOM generation has finished
 * </p>
 */
public interface EnhancementFinishedNotifier {

    /**
     * Notify that an enhancement has been finished
     * @param enhancementRecord
     */
    void notifyFinished(EnhancementRecord enhancementRecord);

}
