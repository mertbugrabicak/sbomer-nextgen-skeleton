package org.jboss.sbomer.sbom.service.adapter.in.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * The main request body for triggering an SBOM generation via the REST API.
 */
public record GenerationRequestsDTO(
        @NotEmpty(message = "At least one generation request must be provided")
        @Valid
        List<GenerationRequestDTO> generationRequests,

        @Valid
        List<PublisherDTO> publishers // This is optional; user can omit or send an empty list
) {}
