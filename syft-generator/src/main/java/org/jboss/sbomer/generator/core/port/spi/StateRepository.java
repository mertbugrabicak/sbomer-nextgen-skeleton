package org.jboss.sbomer.generator.core.port.spi;

import org.jboss.sbomer.generator.core.domain.dto.GenerationRecord;

public interface StateRepository {
    void save(GenerationRecord record);
}
