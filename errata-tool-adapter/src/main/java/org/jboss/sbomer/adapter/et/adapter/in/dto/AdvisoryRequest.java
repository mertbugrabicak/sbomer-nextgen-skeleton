package org.jboss.sbomer.adapter.et.adapter.in.dto;

import jakarta.validation.constraints.NotEmpty;

/**
 * DTO representing the advisory information received via REST API. It is the
 * body of the request.
 * 
 */
public record AdvisoryRequest(@NotEmpty String advisoryId) {

}
