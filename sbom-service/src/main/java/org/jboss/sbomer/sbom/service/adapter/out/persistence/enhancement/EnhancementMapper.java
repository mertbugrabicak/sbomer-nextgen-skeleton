package org.jboss.sbomer.sbom.service.adapter.out.persistence.enhancement;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.sbomer.sbom.service.core.domain.dto.EnhancementRecord;

@ApplicationScoped
public class EnhancementMapper {

    public EnhancementRecord toDomain(EnhancementEntity entity) {
        if (entity == null) return null;

        EnhancementRecord domain = new EnhancementRecord();
        domain.setId(entity.id);
        domain.setEnhancerName(entity.enhancerName);
        domain.setEnhancerVersion(entity.enhancerVersion);
        domain.setIndex(entity.index);
        domain.setCreated(entity.created);
        domain.setUpdated(entity.updated);
        domain.setFinished(entity.finished);
        domain.setStatus(entity.status);
        domain.setResult(entity.result);
        domain.setReason(entity.reason);
        domain.setEvent(entity.event);
        domain.setRequestId(entity.requestId);
        domain.setEnhancedSbomUrls(entity.enhancedSbomUrls);

        return domain;
    }

    public EnhancementEntity toEntity(EnhancementRecord domain) {
        if (domain == null) return null;

        EnhancementEntity entity = EnhancementEntity.findById(domain.getId());
        if (entity == null) {
            entity = new EnhancementEntity();
            entity.id = domain.getId();
            entity.created = domain.getCreated();
        }

        entity.enhancerName = domain.getEnhancerName();
        entity.enhancerVersion = domain.getEnhancerVersion();
        entity.index = domain.getIndex();
        entity.updated = domain.getUpdated();
        entity.finished = domain.getFinished();
        entity.status = domain.getStatus();
        entity.result = domain.getResult();
        entity.reason = domain.getReason();
        entity.event = domain.getEvent();
        entity.requestId = domain.getRequestId();
        entity.enhancedSbomUrls = domain.getEnhancedSbomUrls();

        return entity;
    }
}
