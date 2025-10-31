package org.jboss.sbomer.generation.service.adapter.out;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.jboss.sbomer.generation.service.adapter.out.persistence.GenerationEntity;
import org.jboss.sbomer.generation.service.adapter.out.persistence.GenerationMapper;
import org.jboss.sbomer.generation.service.core.domain.dto.GenerationRecord;
import org.jboss.sbomer.generation.service.core.domain.enums.GenerationStatus;
import org.jboss.sbomer.generation.service.core.port.spi.GenerationRepository;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
public class DatabaseGenerationRepository implements GenerationRepository {

    @Inject
    GenerationMapper mapper;

    @Override
    @Transactional
    public void save(GenerationRecord domainRecord) {
        GenerationEntity entity = mapper.toEntity(domainRecord);
        GenerationEntity.persist(entity);
    }

    @Override
    public GenerationRecord findById(String generationId) {
        GenerationEntity entity = GenerationEntity.findById(generationId);
        return mapper.toDomain(entity);
    }

    @Override
    public List<GenerationRecord> findByStatus(GenerationStatus status) {
        List<GenerationEntity> entities = GenerationEntity.find("status", status).list();
        return entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void update(GenerationRecord domainRecord) {
        // 1. Find the existing, managed entity in the database using its ID.
        GenerationEntity entity = GenerationEntity.findById(domainRecord.getId());

        if (entity == null) {
            log.warn("Attempted to update a GenerationRecord with ID '{}' that does not exist.", domainRecord.getId());
            return;
        }

        // 2. Manually copy the new values from the incoming domain object onto the managed entity.
        entity.status = domainRecord.getStatus();
        entity.updated = domainRecord.getUpdated();
        entity.finished = domainRecord.getFinished();
        entity.reason = domainRecord.getReason();
        entity.result = domainRecord.getResult();
        entity.sbomUrls = domainRecord.getSbomUrls();
        entity.metadata = domainRecord.getMetadata();

        // 3. Panache will automatically save the changes to the 'entity'
        // when this @Transactional method completes. No explicit persist/merge call is needed.
    }
}
