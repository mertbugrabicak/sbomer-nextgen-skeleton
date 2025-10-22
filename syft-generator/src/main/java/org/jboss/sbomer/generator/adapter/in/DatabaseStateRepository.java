package org.jboss.sbomer.generator.adapter.in;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.sbomer.generator.core.domain.dto.GenerationRecord;
import org.jboss.sbomer.generator.core.port.spi.StateRepository;

@ApplicationScoped
public class DatabaseStateRepository implements StateRepository, PanacheRepository<GenerationRecord> {

    @Override
    @Transactional
    public void save(GenerationRecord record) {

    }

}
