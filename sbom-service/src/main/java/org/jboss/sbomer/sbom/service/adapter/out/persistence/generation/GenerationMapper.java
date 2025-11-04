package org.jboss.sbomer.sbom.service.adapter.out.persistence.generation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.sbomer.sbom.service.adapter.out.persistence.enhancement.EnhancementMapper;
import org.jboss.sbomer.sbom.service.core.domain.dto.GenerationRecord;

import java.util.Collections;
import java.util.stream.Collectors;

@ApplicationScoped
public class GenerationMapper {

    @Inject
    EnhancementMapper enhancementMapper; // Inject the new mapper

    // Convert from the database entity to our clean domain object
    public GenerationRecord toDomain(GenerationEntity entity) {
        if (entity == null) return null;

        GenerationRecord domain = new GenerationRecord();
        domain.setId(entity.id);
        domain.setCreated(entity.created);
        domain.setUpdated(entity.updated);
        domain.setFinished(entity.finished);
        domain.setStatus(entity.status);
        domain.setResult(entity.result);
        domain.setReason(entity.reason);
        domain.setEvent(entity.event);
        domain.setMetadata(entity.metadata);

        // --- UPDATED FIELDS ---
        domain.setGeneratorName(entity.generatorName);
        domain.setGeneratorVersion(entity.generatorVersion);
        domain.setRequestId(entity.requestId);
        domain.setGenerationSbomUrls(entity.generationSbomUrls); // Renamed

        // Map the list of enhancement entities to DTOs
        if (entity.enhancements != null) {
            domain.setEnhancements(
                    entity.enhancements.stream()
                            .map(enhancementMapper::toDomain)
                            .collect(Collectors.toList())
            );
        } else {
            domain.setEnhancements(Collections.emptyList());
        }
        // --- END UPDATED FIELDS ---

        return domain;
    }

    // Convert from our clean domain object to the database entity
    public GenerationEntity toEntity(GenerationRecord domain) {
        if (domain == null) return null;

        // Find existing or create new to avoid detaching issues
        GenerationEntity entity = GenerationEntity.findById(domain.getId());
        if (entity == null) {
            entity = new GenerationEntity();
            entity.id = domain.getId();
            entity.created = domain.getCreated();
        }

        entity.updated = domain.getUpdated();
        entity.finished = domain.getFinished();
        entity.status = domain.getStatus();
        entity.result = domain.getResult();
        entity.reason = domain.getReason();
        entity.event = domain.getEvent();
        entity.metadata = domain.getMetadata();

        // --- UPDATED FIELDS ---
        entity.generatorName = domain.getGeneratorName();
        entity.generatorVersion = domain.getGeneratorVersion();
        entity.requestId = domain.getRequestId();
        entity.generationSbomUrls = domain.getGenerationSbomUrls(); // Renamed

        // --- FIX IS HERE ---
        // 1. Create a final variable for use in the lambda
        final GenerationEntity finalEntity = entity;

        // Map the list of enhancement DTOs to entities
        // This handles adding, updating, and removing (due to orphanRemoval=true)
        if (entity.enhancements != null) {
            entity.enhancements.clear();
        }
        if (domain.getEnhancements() != null) {
            entity.enhancements = domain.getEnhancements().stream()
                    .map(enhancementMapper::toEntity)
                    .peek(enhancementEntity -> enhancementEntity.generation = finalEntity) // 2. Use the final variable
                    .collect(Collectors.toList());
        }
        // --- END UPDATED FIELDS ---

        return entity;
    }
}