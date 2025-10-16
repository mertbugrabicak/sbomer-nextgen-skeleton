package org.jboss.sbomer.adapter.et.core.service;

import java.util.ArrayList;
import java.util.List;

import org.jboss.sbomer.adapter.et.core.domain.advisory.Advisory;
import org.jboss.sbomer.adapter.et.core.domain.advisory.Build;
import org.jboss.sbomer.adapter.et.core.domain.generation.GenerationRequest;
import org.jboss.sbomer.adapter.et.core.port.api.AdvisoryHandler;
import org.jboss.sbomer.adapter.et.core.port.spi.ErrataTool;
import org.jboss.sbomer.adapter.et.core.port.spi.GenerationService;
import org.jboss.sbomer.adapter.et.core.port.spi.Koji;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class AdvisoryService implements AdvisoryHandler {
    ErrataTool errataTool;
    GenerationService generationService;
    Koji koji;

    @Inject
    public AdvisoryService(ErrataTool errataTool, GenerationService generationService, Koji koji) {
        this.errataTool = errataTool;
        this.generationService = generationService;
        this.koji = koji;
    }

    @Override
    public List<GenerationRequest> requestGenerations(String advisoryId) {
        log.info("Handling advisory: {}...", advisoryId);

        Advisory advisory = errataTool.getInfo(advisoryId);

        log.debug("Advisory '{}' current status: {}", advisory.id(), advisory.status());

        // TODO: Handle status properly
        // How to handle cases where we would like to filter on status as well as force
        // generations?
        // Do we have cases where we want to ignore status and always generate for REST
        // and do filtering only for UMB? In this case, maybe this method should not
        // take the "status" parameter.

        // TODO: in case of container images we need to understand what is the pullspec
        // Code similar to this should be performed in the ErrataToolService
        // koji.getImageNames(attachedBuilds.stream().map(Build::id).toList());
        // TODO: We need to understand what type of content is attached to the advisory

        List<GenerationRequest> generationRequests = new ArrayList<>();

        // Obtain list of attached builds (in case of standard advisory)
        if (advisory.isTextOnly()) {
            log.info("Advisory '{}' type: text-only", advisory.id());
            // TODO: Handle text-only advisories properly
            // For now we just log and return empty list
        } else {
            log.info("Advisory '{}' type: standard", advisory.id());
            generationRequests.addAll(attachedBuildsToGenerationRequests(advisory.id()));
        }

        // TODO: Use GenerationService to request generations
        // TODO: Ensure we have stored the request before returning
        generationService.requestGenerations(generationRequests);

        log.info("Advisory '{}' handled", advisoryId);

        return generationRequests;
    }

    List<GenerationRequest> attachedBuildsToGenerationRequests(String advisoryId) {
        List<Build> attachedBuilds = errataTool.fetchBuilds(advisoryId);

        log.debug("Advisory '{}' has {} build(s) attached", advisoryId, attachedBuilds.size());

        // Preparing generation requests for attached builds
        return attachedBuilds.stream().map(GenerationRequest::fromBuild)
                .toList();
    }

}
