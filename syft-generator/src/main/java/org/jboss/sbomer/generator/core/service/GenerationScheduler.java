package org.jboss.sbomer.generator.core.service;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.jboss.sbomer.generator.core.domain.dto.GenerationRecord;
import org.jboss.sbomer.generator.core.domain.enums.GenerationStatus;
import org.jboss.sbomer.generator.core.port.spi.StateRepository;

import java.time.Instant;
import java.util.List;

// GenerationEventSource equivalent
@ApplicationScoped
@Slf4j
public class GenerationScheduler {

    @Inject
    StateRepository stateRepository;

    // Just a demo of picking up generations for scheduling

    @Scheduled(every = "5s", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    @Transactional
    public void schedule() {
        log.debug("Scheduler running: Looking for NEW generation records...");

        // 1. Find new work.
        List<GenerationRecord> newRecords = stateRepository.findByStatus(GenerationStatus.NEW);

        if (newRecords.isEmpty()) {
            return;
        }

        log.info("Scheduler found {} new records to schedule.", newRecords.size());

        // 2. Promote each record to SCHEDULED.
        for (GenerationRecord record : newRecords) {
            record.setStatus(GenerationStatus.SCHEDULED);
            record.setUpdated(Instant.now());
            stateRepository.update(record);
        }
    }
}