package org.jboss.sbomer.generation.service.adapter.out.persistence;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.sbomer.generation.service.core.domain.dto.GenerationRecord;

@ApplicationScoped
public class GenerationMapper {

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
        domain.setSbomUrls(entity.sbomUrls);

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
        // NOTE: For updates, we might need to handle collections and maps carefully.
        entity.metadata = domain.getMetadata();
        entity.sbomUrls = domain.getSbomUrls();

        return entity;
    }
}
