package org.jboss.sbomer.generation.service.core.domain.enums;

public enum GenerationStatus {
    NEW, SCHEDULED, INITIALIZING, INITIALIZED, GENERATING, FINISHED, FAILED;

    public static GenerationStatus fromName(String phase) {
        return GenerationStatus.valueOf(phase.toUpperCase());
    }

    public String toName() {
        return this.name().toUpperCase();
    }

    public boolean isOlderThan(GenerationStatus desiredStatus) {
        if (desiredStatus == null) {
            return false;
        }

        return desiredStatus.ordinal() > this.ordinal();
    }

    public boolean isFinal() {
        return this.equals(FAILED) || this.equals(FINISHED);
    }
}