package org.jboss.sbomer.dispatcher.adapter.out.persistence.models;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.jboss.sbomer.dispatcher.core.domain.dto.enums.RequestStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "request")
public class RequestEntity extends PanacheEntityBase {

    @Id
    public String id; // The RequestId

    public Instant createdAt;

    @Enumerated(EnumType.STRING)
    public RequestStatus status;

    public int totalGenerations;
    public int completedGenerations;

    // Stores the serialized RequestsCreated event
    @Column(columnDefinition = "TEXT")
    public String originalRequestEvent;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    public List<GenerationEntity> generations = new ArrayList<>();
}
