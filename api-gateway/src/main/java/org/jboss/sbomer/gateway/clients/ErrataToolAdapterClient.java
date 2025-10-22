package org.jboss.sbomer.gateway.clients;

import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import com.fasterxml.jackson.databind.JsonNode;

@RegisterRestClient(configKey = "errata-tool-adapter") // Links to application.properties
@Path("/v1/errata-tool")
public interface ErrataToolAdapterClient {

    @POST
    @Path("/generate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Response generate(JsonNode payload);

}