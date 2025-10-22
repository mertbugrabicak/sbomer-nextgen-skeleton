package org.jboss.sbomer.generator.core.domain.dto;

public record TaskStatus(
        boolean isFinished,
        boolean isSuccessful,
        int retryCount,
        String errorMessage
) {}
