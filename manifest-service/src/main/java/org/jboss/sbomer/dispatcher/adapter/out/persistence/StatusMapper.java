package org.jboss.sbomer.dispatcher.adapter.out.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.jboss.sbomer.dispatcher.adapter.out.persistence.models.GenerationEntity;
import org.jboss.sbomer.dispatcher.adapter.out.persistence.models.ManifestEntity;
import org.jboss.sbomer.dispatcher.adapter.out.persistence.models.RequestEntity;
import org.jboss.sbomer.dispatcher.core.domain.dto.GenerationRecord;
import org.jboss.sbomer.dispatcher.core.domain.dto.ManifestRecord;
import org.jboss.sbomer.dispatcher.core.domain.dto.RequestRecord;
import org.jboss.sbomer.events.kafka.common.PublisherSpec;
import org.jboss.sbomer.events.kafka.common.TargetSpec;
import org.jboss.sbomer.events.kafka.handler.RequestsCreated;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
public class StatusMapper {

    @Inject
    ObjectMapper objectMapper;

    // --- Conversion from Entity (Database) to Record (Domain) ---

    public RequestRecord toDomain(RequestEntity entity) {
        if (entity == null) return null;

        RequestRecord domain = new RequestRecord();
        domain.setId(entity.id);
        domain.setCreatedAt(entity.createdAt);
        domain.setStatus(entity.status);
        domain.setTotalGenerations(entity.totalGenerations);
        domain.setCompletedGenerations(entity.completedGenerations);

        // Deserialize the event from JSON string
        domain.setOriginalRequestEvent(deserializeRequestsCreated(entity.originalRequestEvent));

        if (entity.generations != null) {
            domain.setGenerations(entity.generations.stream().map(this::toDomain).collect(Collectors.toList()));
        }
        return domain;
    }

    public GenerationRecord toDomain(GenerationEntity entity) {
        if (entity == null) return null;

        GenerationRecord domain = new GenerationRecord();
        domain.setId(entity.id);
        if (entity.request != null) {
            domain.setRequestId(entity.request.id);
        }
        domain.setUpdatedAt(entity.updatedAt);
        domain.setTargetType(entity.targetType);
        domain.setTargetIdentifier(entity.targetIdentifier);
        domain.setStatus(entity.status);
        domain.setReason(entity.reason);
        // ... map manifests ...
        return domain;
    }

    //... toDomain(ManifestEntity) ...

    // --- Conversion from Record (Domain) to Entity (Database) ---

    public RequestEntity toEntity(RequestRecord domain) {
        if (domain == null) return null;

        RequestEntity entity = new RequestEntity();
        entity.id = domain.getId();
        entity.createdAt = domain.getCreatedAt();
        entity.status = domain.getStatus();
        entity.totalGenerations = domain.getTotalGenerations();
        entity.completedGenerations = domain.getCompletedGenerations();

        // Serialize the event to a JSON string
        entity.originalRequestEvent = serialize(domain.getOriginalRequestEvent());

        if (domain.getGenerations() != null) {
            entity.generations = domain.getGenerations().stream()
                    .map(genRecord -> toEntity(genRecord, entity))
                    .collect(Collectors.toList());
        }
        return entity;
    }

    public GenerationEntity toEntity(GenerationRecord domain, RequestEntity parentRequest) {
        if (domain == null) return null;

        GenerationEntity entity = new GenerationEntity();
        entity.id = domain.getId();
        entity.request = parentRequest;
        entity.updatedAt = domain.getUpdatedAt();
        entity.targetType = domain.getTargetType();
        entity.targetIdentifier = domain.getTargetIdentifier();
        entity.status = domain.getStatus();
        entity.reason = domain.getReason();
        // ... map manifests ...
        return entity;
    }

    //... toEntity(ManifestRecord, GenerationEntity) ...
    //... updateEntityFromDomain(...) ...

    // --- Private JSON Helper Methods ---

    private String serialize(Object object) {
        try {
            if (object == null) return null;
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON string", e);
            throw new RuntimeException(e);
        }
    }

    private RequestsCreated deserializeRequestsCreated(String json) {
        try {
            if (json == null) return null;
            return objectMapper.readValue(json, RequestsCreated.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize RequestsCreated from JSON string", e);
            throw new RuntimeException(e);
        }
    }
}