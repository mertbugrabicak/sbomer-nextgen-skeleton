package org.jboss.sbomer.generation.service.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents the target for an SBOM generation request from an end-user.
 */
public record TargetDTO(
        @NotBlank(message = "Target type must be provided")
        @JsonProperty("type")
        String type,

        @NotBlank(message = "Target identifier must be provided")
        @JsonProperty("identifier")
        String identifier
) {}
