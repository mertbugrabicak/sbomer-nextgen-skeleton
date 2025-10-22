package org.jboss.sbomer.gateway;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.sbomer.gateway.clients.ErrataToolAdapterClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class GenerationGatewayResourceTest {

    // 1. Inject a mock version of your downstream client
    @InjectMock
    @RestClient
    ErrataToolAdapterClient errataClient;

    @Test
    void testErrataRouting() {
        // 2. Define the behavior of the mock
        // When the 'generate' method is called, return a 200 OK with a specific JSON body.
        String mockResponse = "{\"status\":\"OK\", \"id\":\"errata-123\"}";
        Mockito.when(errataClient.generate(Mockito.any()))
                .thenReturn(Response.ok(mockResponse).build());

        // 3. Create the request body to send to the gateway
        String requestBody = """
                {
                  "sourceType": "errata",
                  "payload": {
                    "advisoryId": 12345
                  }
                }
                """;

        // 4. Perform the actual test
        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/v1/generate") // Use POST, not GET
                .then()
                .statusCode(200) // Expect a 200 from the mock
                .body("status", is("OK")) // Verify the response from the mock
                .body("id", is("errata-123"));
    }

    @Test
    void testUnknownSourceType() {
        // Test the "default" case in your switch statement
        String requestBody = """
                {
                  "sourceType": "unknown-source",
                  "payload": {}
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/v1/generate")
                .then()
                .statusCode(400) // Expect a 400 Bad Request
                .body("error", is("Unknown sourceType: unknown-source"));
    }
}