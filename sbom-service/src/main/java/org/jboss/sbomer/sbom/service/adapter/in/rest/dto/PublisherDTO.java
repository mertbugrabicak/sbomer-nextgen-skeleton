package org.jboss.sbomer.sbom.service.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

/**
 * Represents a destination where the generated SBOM should be published.
 */
public record PublisherDTO(
        @NotBlank(message = "Publisher name must be provided")
        String name,
        Map<String, String> options
) {}
