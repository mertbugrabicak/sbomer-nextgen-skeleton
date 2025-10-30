package org.jboss.sbomer.adapter.et.adapter.out;

import java.util.ArrayList;
import java.util.List;

import org.jboss.sbomer.adapter.et.core.domain.advisory.Advisory;
import org.jboss.sbomer.adapter.et.core.domain.advisory.Build;
import org.jboss.sbomer.adapter.et.core.port.spi.ErrataTool;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class ErrataToolService implements ErrataTool {
    @Override
    public List<Build> fetchBuilds(String advisoryId) {
        log.info("Fetching attached builds for advisory with ID: '{}'...", advisoryId);

        // Simulate getting a list of varied builds from an advisory from ET
        List<Build> builds = new ArrayList<>();
        builds.add(new Build(456L, "container-1.0", "CONTAINER_IMAGE", "ABC"));
        builds.add(new Build(789L, "other-container-2.1", "CONTAINER_IMAGE", "XYZ"));

        log.debug("Fetched {} builds for advisory with ID: '{}'", builds.size(), advisoryId);

        return builds;
    }

    @Override
    public Advisory getInfo(String advisoryId) {
        // so we can test errors
        if (advisoryId.equals("456")) {
            throw new RuntimeException("advisoryId 456 is not a valid advisory id");
        }
        return new Advisory(advisoryId, "QE", false);
    }
}
