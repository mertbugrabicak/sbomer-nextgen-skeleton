package org.jboss.sbomer.adapter.et.adapter.in.dto;

import java.util.List;

import org.jboss.sbomer.adapter.et.core.domain.generation.GenerationRequest;

/**
 * DTO representing the response of the REST API after requesting generations
 * for advisory.
 * 
 */
public record AdvisoryRequestResponse(List<GenerationRequest> generations) {

}
