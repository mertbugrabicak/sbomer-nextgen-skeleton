package org.jboss.sbomer.sbom.service.adapter.out.persistence.generation;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.jboss.sbomer.sbom.service.adapter.out.persistence.enhancement.EnhancementEntity;
import org.jboss.sbomer.sbom.service.adapter.out.persistence.MapToStringConverter;
import org.jboss.sbomer.sbom.service.core.domain.enums.GenerationResult;
import org.jboss.sbomer.sbom.service.core.domain.enums.GenerationStatus;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "generation", indexes = {
        @Index(name = "idx_generation_requestid", columnList = "requestId")
})
public class GenerationEntity extends PanacheEntityBase {

    @Id
    public String id;

    public String generatorName;
    public String generatorVersion;
    public String requestId;

    public Instant created;
    public Instant updated;
    public Instant finished;

    @Column(columnDefinition = "TEXT")
    public String event;

    @Enumerated(EnumType.STRING)
    public GenerationStatus status;

    @Enumerated(EnumType.STRING)
    public GenerationResult result;

    @Column(columnDefinition = "TEXT")
    public String reason;

    @Convert(converter = MapToStringConverter.class)
    @Column(columnDefinition = "TEXT")
    public Map<String, String> metadata;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "generation_sbom_urls", joinColumns = @JoinColumn(name = "generation_id"))
    @Column(name = "sbom_url", columnDefinition = "TEXT")
    // ** RENAMED FIELD **
    public List<String> generationSbomUrls;

    // --- NEW RELATIONSHIP ---
    @OneToMany(
            mappedBy = "generation",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @OrderBy("index ASC ") // Ensures the list is always ordered by the index
    public List<EnhancementEntity> enhancements;
    // --- END NEW RELATIONSHIP ---
}