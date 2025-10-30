package org.jboss.sbomer.generator.adapter.out.persistence;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.sbomer.generator.core.domain.dto.GenerationRecord;

@ApplicationScoped
public class GenerationMapper {

    // Convert from the database entity to our clean domain object
    public GenerationRecord toDomain(GenerationEntity entity) {
        if (entity == null) return null;

        GenerationRecord domain = new GenerationRecord();
        domain.setId(entity.id);
        domain.setCreated(entity.created);
        domain.setUpdated(entity.updated);   // <-- FIX: Added mapping for 'updated'
        domain.setFinished(entity.finished); // <-- FIX: Added mapping for 'finished'
        domain.setStatus(entity.status);
        domain.setResult(entity.result);     // <-- FIX: Added mapping for 'result'
        domain.setReason(entity.reason);     // <-- FIX: Added mapping for 'reason'
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
        entity.result = domain.getResult();     // <-- FIX: Added mapping for 'result'
        entity.reason = domain.getReason();     // <-- FIX: Added mapping for 'reason'
        entity.event = domain.getEvent();
        // NOTE: For updates, you might need to handle collections and maps carefully.
        // For this simple case, direct assignment is fine.
        entity.metadata = domain.getMetadata();
        entity.sbomUrls = domain.getSbomUrls();

        return entity;
    }
}
