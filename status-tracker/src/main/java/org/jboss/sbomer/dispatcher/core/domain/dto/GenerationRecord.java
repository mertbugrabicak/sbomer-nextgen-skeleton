package org.jboss.sbomer.dispatcher.core.domain.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.common.protocol.types.Field;
import org.jboss.sbomer.dispatcher.core.domain.dto.enums.GenerationStatus;
import org.jboss.sbomer.events.kafka.common.TargetSpec;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class GenerationRecord {

    private String id;
    private String requestId;
    private Instant updatedAt;
    private String targetType;
    private String targetIdentifier;
    private GenerationStatus status;
    private String reason;
    private List<ManifestRecord> manifests;

}
