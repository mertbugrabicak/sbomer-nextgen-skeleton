package org.jboss.sbomer.dispatcher.core.domain.dto;

import lombok.Getter;
import lombok.Setter;
import org.jboss.sbomer.dispatcher.core.domain.dto.enums.RequestStatus;
import org.jboss.sbomer.events.kafka.common.PublisherSpec;
import org.jboss.sbomer.events.kafka.handler.RequestsCreated;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class RequestRecord {

    private String id;
    private Instant createdAt;
    private RequestStatus status;
    private int totalGenerations;
    private int completedGenerations;
    private RequestsCreated originalRequestEvent;
    private List<GenerationRecord> generations;

}
