package org.jboss.sbomer.dispatcher.adapter.out.persistence.models;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.jboss.sbomer.dispatcher.core.domain.dto.enums.GenerationStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "generation")
public class GenerationEntity extends PanacheEntityBase {

    @Id
    public String id; // The GenerationId

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    public RequestEntity request;

    public Instant updatedAt;

    // targetspec denormalized fields
    public String targetType;
    public String targetIdentifier;

    @Enumerated(EnumType.STRING)
    public GenerationStatus status;

    @Column(columnDefinition = "TEXT")
    public String reason;

    @OneToMany(mappedBy = "generation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    public List<ManifestEntity> manifests = new ArrayList<>();
}