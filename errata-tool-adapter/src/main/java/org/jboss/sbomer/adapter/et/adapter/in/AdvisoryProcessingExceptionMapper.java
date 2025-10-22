package org.jboss.sbomer.adapter.et.adapter.in;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.sbomer.adapter.et.core.domain.exception.AdvisoryProcessingException;

// The @Provider annotation registers this with JAX-RS
@Provider
public class AdvisoryProcessingExceptionMapper implements ExceptionMapper<AdvisoryProcessingException> {

    // A simple DTO for the error response body
    public record ErrorResponse(String message) {}

    @Override
    public Response toResponse(AdvisoryProcessingException exception) {
        // Create a JSON response body
        ErrorResponse errorBody = new ErrorResponse(exception.getMessage());

        // Return a 500 Internal Server Error response with the JSON body
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorBody)
                .build();
    }
}
