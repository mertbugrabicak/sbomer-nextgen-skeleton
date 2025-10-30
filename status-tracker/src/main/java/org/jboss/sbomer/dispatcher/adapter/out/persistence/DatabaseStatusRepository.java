package org.jboss.sbomer.dispatcher.adapter.out.persistence;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.jboss.sbomer.dispatcher.adapter.out.persistence.models.GenerationEntity;
import org.jboss.sbomer.dispatcher.adapter.out.persistence.models.RequestEntity;
import org.jboss.sbomer.dispatcher.core.domain.dto.GenerationRecord;
import org.jboss.sbomer.dispatcher.core.domain.dto.RequestRecord;
import org.jboss.sbomer.dispatcher.core.port.spi.StatusRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
public class DatabaseStatusRepository implements StatusRepository {

    @Inject
    StatusMapper mapper;

    @Override
    @Transactional
    public void save(RequestRecord request) {
        log.debug("Saving new RequestRecord with ID '{}'", request.getId());
        RequestEntity entity = mapper.toEntity(request);
        entity.persist();
    }

    @Override
    public GenerationRecord findGenerationById(String generationId) {
        GenerationEntity entity = GenerationEntity.findById(generationId);
        return mapper.toDomain(entity);
    }

    @Override
    public RequestRecord findRequestById(String requestId) {
        RequestEntity entity = RequestEntity.findById(requestId);
        return mapper.toDomain(entity);
    }

    @Override
    @Transactional
    public void update(RequestRecord request) {
        log.debug("Updating RequestRecord with ID '{}'", request.getId());
        RequestEntity entity = RequestEntity.findById(request.getId());

        if (entity == null) {
            log.warn("Attempted to update a RequestRecord with ID '{}' that does not exist.", request.getId());
            return;
        }

        // Update the parent RequestEntity's direct fields
        entity.status = request.getStatus();
        entity.completedGenerations = request.getCompletedGenerations();

        // Efficiently update the child GenerationEntity objects
        // Create a map for quick lookups
        Map<String, GenerationEntity> entityMap = entity.generations.stream()
                .collect(Collectors.toMap(gen -> gen.id, Function.identity()));

        for (GenerationRecord genRecord : request.getGenerations()) {
            GenerationEntity genEntity = entityMap.get(genRecord.getId());
            if (genEntity != null) {
                // Update fields on the managed entity
                genEntity.status = genRecord.getStatus();
                genEntity.updatedAt = genRecord.getUpdatedAt();
                genEntity.reason = genRecord.getReason();

                // Mapper handles the creation of ManifestEntity children
                genEntity.manifests = mapper.toEntity(genRecord, entity).manifests;
            }
        }
        // Panache automatically saves all changes to managed entities when the transaction commits.
    }

    @Override
    public List<RequestRecord> findAllRequests() {
        return RequestEntity.<RequestEntity>listAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
