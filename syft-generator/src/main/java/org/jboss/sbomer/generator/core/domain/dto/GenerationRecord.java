package org.jboss.sbomer.generator.core.domain.dto;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;
import org.jboss.sbomer.generator.core.domain.enums.GenerationResult;
import org.jboss.sbomer.generator.core.domain.enums.GenerationStatus;

/**
 * Representation of the Generation entity.
 */
@Slf4j
public record GenerationRecord(String id, Instant created, Instant updated, Instant finished,
                               ObjectNode request, Map<String, String> metadata, GenerationStatus status, GenerationResult result,
                               String reason) { }
