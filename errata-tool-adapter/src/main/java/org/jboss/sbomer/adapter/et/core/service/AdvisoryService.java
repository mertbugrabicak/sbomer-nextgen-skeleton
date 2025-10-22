package org.jboss.sbomer.adapter.et.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.sbomer.adapter.et.core.domain.advisory.Advisory;
import org.jboss.sbomer.adapter.et.core.domain.advisory.Build;
import org.jboss.sbomer.adapter.et.core.domain.exception.AdvisoryProcessingException;
import org.jboss.sbomer.adapter.et.core.domain.generation.GenerationRequest;
import org.jboss.sbomer.adapter.et.core.port.api.AdvisoryHandler;
import org.jboss.sbomer.adapter.et.core.port.spi.ErrataTool;
import org.jboss.sbomer.adapter.et.core.port.spi.FailureNotifier;
import org.jboss.sbomer.adapter.et.core.port.spi.GenerationRequestService;
import org.jboss.sbomer.adapter.et.core.port.spi.Koji;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.jboss.sbomer.adapter.et.core.utility.FailureUtility;
import org.jboss.sbomer.events.kafka.common.FailureSpec; // Assuming this is the correct import for the Avro-generated class

@ApplicationScoped
@Slf4j
public class AdvisoryService implements AdvisoryHandler {
    ErrataTool errataTool;
    GenerationRequestService generationRequestService;
    Koji koji;
    FailureNotifier failureNotifier;

    @ConfigProperty(name = "sbomer.publisher.atlas.build")
    public String ATLAS_BUILD_PUBLISHER;
    @ConfigProperty(name = "sbomer.publisher.atlas.release")
    public String ATLAS_RELEASE_PUBLISHER;

    @Inject
    public AdvisoryService(ErrataTool errataTool, GenerationRequestService generationRequestService, Koji koji, FailureNotifier failureNotifier) {
        this.errataTool = errataTool;
        this.generationRequestService = generationRequestService;
        this.koji = koji;
        this.failureNotifier = failureNotifier;
    }

    @Override
    public List<GenerationRequest> requestGenerations(String advisoryId) {
        log.info("Handling advisory: {}...", advisoryId);

        // ** Start of the try-catch block **
        try {
            Advisory advisory = errataTool.getInfo(advisoryId);

            log.debug("Advisory '{}' current status: {}", advisory.id(), advisory.status());
            List<String> publishers = new ArrayList<>();
            if (advisory.status().equals("QE")) {
                publishers.add(ATLAS_BUILD_PUBLISHER);
                log.debug("Advisory '{}' is QE, adding {} publisher", advisory.id(), ATLAS_BUILD_PUBLISHER);
            } else if (advisory.status().equals("SHIPPED_LIVE")) {
                publishers.add(ATLAS_RELEASE_PUBLISHER);
                log.debug("Advisory '{}' is SHIPPED_LIVE, adding {} publisher", advisory.id(), ATLAS_RELEASE_PUBLISHER);
            }

            List<GenerationRequest> generationRequests = new ArrayList<>();

            if (advisory.isTextOnly()) {
                log.info("Advisory '{}' type: text-only", advisory.id());
                // TODO: Handle text-only advisories properly
            } else {
                log.info("Advisory '{}' type: standard", advisory.id());
                generationRequests.addAll(attachedBuildsToGenerationRequests(advisory.id()));
            }

            generationRequestService.requestGenerations(generationRequests, publishers);

            log.info("Advisory '{}' handled successfully", advisoryId);

            return generationRequests;

            // ** Catch block to handle any exception **
        } catch (Exception e) {
            log.error("Failed to handle advisory '{}' due to an unexpected error: {}", advisoryId, e.getMessage(), e);

            // 1. Build the failure details.
            FailureSpec failure = FailureUtility.buildFailureSpecFromException(e);

            // 2. Notify the internal failure system (the source is null).
            failureNotifier.notify(failure, null);

            // 3. IMPORTANT: Throw the custom domain exception to propagate the error upward.
            throw new AdvisoryProcessingException("Failed to process advisory " + advisoryId, e);
        }
    }

    List<GenerationRequest> attachedBuildsToGenerationRequests(String advisoryId) {
        // No try-catch needed here; any exception will be caught by the calling method.
        List<Build> attachedBuilds = errataTool.fetchBuilds(advisoryId);

        log.debug("Advisory '{}' has {} build(s) attached", advisoryId, attachedBuilds.size());

        return attachedBuilds.stream().map(GenerationRequest::fromBuild)
                .toList();
    }
}