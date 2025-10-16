package org.jboss.sbomer.adapter.et.core.port.spi;

import java.util.List;

import org.jboss.sbomer.adapter.et.core.domain.advisory.Advisory;
import org.jboss.sbomer.adapter.et.core.domain.advisory.Build;

/**
 * <p>
 * Primary interface representing operations related to interaction with Errata
 * Tool API.
 * </p>
 * 
 */
public interface ErrataTool {
    public Advisory getInfo(String advisoryId);

    /**
     * Obtains list of builds attached to the given advisory.
     * 
     * @param advisoryId The integer-based advisory ID.
     * @return A list of builds associated with the advisory or empty list if none
     *         found.
     */
    public List<Build> fetchBuilds(String advisoryId);

}
