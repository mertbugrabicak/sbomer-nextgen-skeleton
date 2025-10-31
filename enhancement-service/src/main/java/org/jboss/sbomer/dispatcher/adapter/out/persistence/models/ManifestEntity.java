package org.jboss.sbomer.dispatcher.adapter.out.persistence.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "manifest")
public class ManifestEntity extends PanacheEntity { // Extends PanacheEntity for an automatic Long ID

    // Defines the many-to-one relationship back to GenerationEntity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generation_id")
    public GenerationEntity generation;

    @Column(columnDefinition = "TEXT")
    public String url;
}
