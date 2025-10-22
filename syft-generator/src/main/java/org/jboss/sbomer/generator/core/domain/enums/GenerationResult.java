package org.jboss.sbomer.generator.core.domain.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
// TODO: We should update these. Or maybe these should NOT be added here at all? These are specific to generators.
public enum GenerationResult {
    SUCCESS(0),
    ERR_GENERAL(1),
    ERR_CONFIG_INVALID(2),
    ERR_CONFIG_MISSING(3),
    ERR_INDEX_INVALID(4),
    ERR_GENERATION(5),
    ERR_POST(6),
    ERR_OOM(7),
    ERR_SYSTEM(99),
    ERR_MULTI(100);

    final int code;

    GenerationResult(int code) {
        this.code = code;
    }

    public static Optional<GenerationResult> fromCode(int code) {
        return Arrays.stream(GenerationResult.values()).filter(r -> r.code == code).findFirst();

    }
}
