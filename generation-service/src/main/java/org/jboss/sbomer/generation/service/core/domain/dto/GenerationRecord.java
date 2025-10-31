    package org.jboss.sbomer.generation.service.core.domain.dto;

    import lombok.Getter;
    import lombok.Setter;
    import org.jboss.sbomer.generation.service.core.domain.enums.GenerationResult;
    import org.jboss.sbomer.generation.service.core.domain.enums.GenerationStatus;

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
        private GenerationResult result;
        private String reason;
        private String event;
        private String requestId;
        private Map<String, String> metadata;
        private List<String> sbomUrls;

    }
