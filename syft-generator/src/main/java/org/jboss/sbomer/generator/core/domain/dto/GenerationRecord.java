package org.jboss.sbomer.generator.core.domain.dto;

import lombok.Getter;
import lombok.Setter;
import org.jboss.sbomer.generator.core.domain.enums.GenerationStatus;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class GenerationRecord {

    private String id;
    private Instant created;
    private Instant updated;
    private Instant finished;
    private GenerationStatus status;
    private String event; // The serialized GenerationCreated event
    private Map<String, String> metadata;
    private List<String> sbomUrls;

}
