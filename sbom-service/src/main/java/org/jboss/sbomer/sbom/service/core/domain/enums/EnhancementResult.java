package org.jboss.sbomer.sbom.service.core.domain.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum EnhancementResult {
    SUCCESS(0),
    ERR_GENERAL(1),
    ERR_CONFIG_INVALID(2),
    ERR_ENHANCEMENT(5);

    final int code;

    EnhancementResult(int code) {
        this.code = code;
    }

    public static Optional<EnhancementResult> fromCode(int code) {
        return Arrays.stream(EnhancementResult.values()).filter(r -> r.code == code).findFirst();
    }
}
