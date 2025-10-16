package org.jboss.sbomer.adapter.et.adapter.in;

import java.util.List;

import org.jboss.sbomer.adapter.et.adapter.in.dto.AdvisoryRequest;
import org.jboss.sbomer.adapter.et.adapter.in.dto.AdvisoryRequestResponse;
import org.jboss.sbomer.adapter.et.core.domain.generation.GenerationRequest;
import org.jboss.sbomer.adapter.et.core.port.api.AdvisoryHandler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO: Establish convention to follow for REST endpoint naming across SBOMer
 */
@Slf4j
@Path("/v1/errata-tool")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class RestAdvisoryHandler {

    private AdvisoryHandler advisoryHandler;

    @Inject
    RestAdvisoryHandler(AdvisoryHandler advisoryHandler) {
        this.advisoryHandler = advisoryHandler;
    }

    @POST
    @Path("/generate")
    public AdvisoryRequestResponse requestAdvisory(@Valid AdvisoryRequest advisoryInfo) {
        log.debug("Submitted a REST request to handle update of advisory info: {}", advisoryInfo);

        // Request generations for the advisory
        List<GenerationRequest> generations = advisoryHandler.requestGenerations(advisoryInfo.advisoryId());

        // Return a response containing generation requests details
        return new AdvisoryRequestResponse(generations);
    }

}
