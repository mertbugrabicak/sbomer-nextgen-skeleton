package org.jboss.sbomer.sbom.service.adapter.in.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a single generation task within a larger request.
 * The user only needs to specify the target.
 */
public record GenerationRequestDTO(
        @NotNull(message = "A target must be specified for each generation request")
        @Valid // This ensures validation rules on TargetDTO are also checked
        TargetDTO target
) {}
