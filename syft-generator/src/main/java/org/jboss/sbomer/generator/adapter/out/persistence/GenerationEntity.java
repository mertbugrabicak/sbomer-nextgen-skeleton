package org.jboss.sbomer.generator.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.jboss.sbomer.generator.core.domain.enums.GenerationResult;
import org.jboss.sbomer.generator.core.domain.enums.GenerationStatus;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "generation")
public class GenerationEntity extends PanacheEntityBase {

    @Id
    public String id;

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

    @Convert(converter = MapToStringConverter.class) // A simple converter for the map
    @Column(columnDefinition = "TEXT")
    public Map<String, String> metadata;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "generation_sbom_urls", joinColumns = @JoinColumn(name = "generation_id"))
    @Column(name = "sbom_url", columnDefinition = "TEXT")
    public List<String> sbomUrls;
}
