package org.jboss.sbomer.adapter.et.core.port.spi;

import java.util.List;
import java.util.Map;

public interface Koji {
    /**
     * Fetches container image names for the given build IDs.
     * 
     * @param buildIds List of build IDs.
     * @return Map of build ID to image name.
     */
    public Map<Long, String> getImageNames(List<Long> buildIds);
}
