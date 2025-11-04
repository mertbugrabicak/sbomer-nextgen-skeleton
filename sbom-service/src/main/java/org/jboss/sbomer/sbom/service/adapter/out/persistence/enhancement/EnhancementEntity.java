package org.jboss.sbomer.sbom.service.adapter.out.persistence.enhancement;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.jboss.sbomer.sbom.service.adapter.out.persistence.generation.GenerationEntity;
import org.jboss.sbomer.sbom.service.core.domain.enums.EnhancementResult;
import org.jboss.sbomer.sbom.service.core.domain.enums.EnhancementStatus;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "enhancement", indexes = {
        @Index(name = "idx_enhancement_generation", columnList = "generation_id")
})
public class EnhancementEntity extends PanacheEntityBase {

    @Id
    public String id;

    public String enhancerName;
    public String enhancerVersion;

    @Column(name = "enhancement_index") // "index" is often a reserved SQL keyword
    public int index;

    public Instant created;
    public Instant updated;
    public Instant finished;

    @Enumerated(EnumType.STRING)
    public EnhancementStatus status;

    @Enumerated(EnumType.STRING)
    public EnhancementResult result;

    @Column(columnDefinition = "TEXT")
    public String reason;

    @Column(columnDefinition = "TEXT")
    public String event;

    public String requestId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "enhancement_sbom_urls", joinColumns = @JoinColumn(name = "enhancement_id"))
    @Column(name = "sbom_url", columnDefinition = "TEXT")
    public List<String> enhancedSbomUrls;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "generation_id", nullable = false)
    public GenerationEntity generation;
}
