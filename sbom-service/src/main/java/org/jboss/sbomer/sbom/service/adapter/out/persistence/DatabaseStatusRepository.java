package org.jboss.sbomer.sbom.service.adapter.out.persistence;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.jboss.sbomer.sbom.service.adapter.out.persistence.enhancement.EnhancementEntity;
import org.jboss.sbomer.sbom.service.adapter.out.persistence.enhancement.EnhancementMapper;
import org.jboss.sbomer.sbom.service.adapter.out.persistence.generation.GenerationEntity;
import org.jboss.sbomer.sbom.service.adapter.out.persistence.generation.GenerationMapper;
import org.jboss.sbomer.sbom.service.core.domain.dto.EnhancementRecord;
import org.jboss.sbomer.sbom.service.core.domain.dto.GenerationRecord;
import org.jboss.sbomer.sbom.service.core.domain.enums.EnhancementStatus;
import org.jboss.sbomer.sbom.service.core.domain.enums.GenerationStatus;
import org.jboss.sbomer.sbom.service.core.port.spi.StatusRepository;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
public class DatabaseStatusRepository implements StatusRepository {

    @Inject
    GenerationMapper generationMapper;

    @Inject
    EnhancementMapper enhancementMapper;

    // --- GenerationRecord Methods ---

    @Override
    @Transactional
    public void saveGeneration(GenerationRecord record) {
        GenerationEntity entity = generationMapper.toEntity(record);
        GenerationEntity.persist(entity);
    }

    @Override
    public GenerationRecord findGenerationById(String generationId) {
        GenerationEntity entity = GenerationEntity.findById(generationId);
        return generationMapper.toDomain(entity);
    }

    @Override
    public List<GenerationRecord> findByGenerationStatus(GenerationStatus status) {
        List<GenerationEntity> entities = GenerationEntity.find("status", status).list();
        return entities.stream()
                .map(generationMapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * This method updates the GenerationRecord *and* its entire list of enhancements.
     */
    @Override
    @Transactional
    public void updateGeneration(GenerationRecord record) {
        // The mapper's toEntity method is "smart" and handles finding the existing
        // entity and syncing its entire state, including the enhancements list.
        GenerationEntity entity = generationMapper.toEntity(record);
        // We just need to persist the result. Panache's persist() handles both
        // create and merge, which is what we want.
        GenerationEntity.persist(entity);
    }

    // --- EnhancementRecord Methods ---

    @Override
    @Transactional
    public void saveEnhancement(EnhancementRecord record) {
        EnhancementEntity entity = enhancementMapper.toEntity(record);
        EnhancementEntity.persist(entity);
    }

    @Override
    public EnhancementRecord findEnhancementById(String enhancementId) {
        EnhancementEntity entity = EnhancementEntity.findById(enhancementId);
        return enhancementMapper.toDomain(entity);
    }

    @Override
    public List<EnhancementRecord> findByEnhancementStatus(EnhancementStatus status) {
        List<EnhancementEntity> entities = EnhancementEntity.find("status", status).list();
        return entities.stream()
                .map(enhancementMapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * This method updates a *single* enhancement.
     * It does NOT update the parent GenerationRecord.
     */
    @Override
    @Transactional
    public void updateEnhancement(EnhancementRecord record) {
        // The mapper's toEntity method will find the existing entity
        // by its ID and map all fields, including linking the parent.
        EnhancementEntity entity = enhancementMapper.toEntity(record);
        EnhancementEntity.persist(entity);
    }
}