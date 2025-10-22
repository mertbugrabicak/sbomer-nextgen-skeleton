package org.jboss.sbomer.gateway;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import com.fasterxml.jackson.databind.JsonNode;
import org.jboss.sbomer.gateway.clients.ErrataToolAdapterClient;

@Path("/v1/generate")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GenerationGatewayResource {

    @RestClient // Inject the client we defined
    ErrataToolAdapterClient errataClient;

    @POST
    public Response routeGenerationRequest(JsonNode requestBody) {
        // 1. Get the sourceType from the JSON payload
        String sourceType = requestBody.path("sourceType").asText();

        // 2. Decide where to send the request
        switch (sourceType) {
            case "errata":
                // 3. Forward the request payload to the Errata Tool Adapter
                JsonNode payload = requestBody.path("payload");
                return errataClient.generate(payload);

            default:
                String errorMessage = "{\"error\":\"Unknown sourceType: " + sourceType + "\"}";
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorMessage)
                        .build();
        }
    }
}