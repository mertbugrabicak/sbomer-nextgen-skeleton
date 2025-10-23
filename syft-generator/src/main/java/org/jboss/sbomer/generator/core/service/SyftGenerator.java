package org.jboss.sbomer.generator.core.service;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.jboss.sbomer.generator.core.domain.dto.GenerationRecord;
import org.jboss.sbomer.generator.core.domain.enums.GenerationStatus;
import org.jboss.sbomer.generator.core.port.spi.GenerationResultPublisher;
import org.jboss.sbomer.generator.core.port.spi.StateRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class SyftGenerator extends AbstractTektonController {

    @Inject
    StateRepository stateRepository;

    @Inject
    GenerationResultPublisher resultPublisher;

    @Scheduled(every = "7s", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void scheduleReconciliation() {
        super.reconcileLoop();
    }

    @Override
    @Transactional
    protected void reconcileScheduled(GenerationRecord record) {
        log.info("SyftGenerator: Starting job for Generation ID '{}'.", record.getId());

        // --- FIX: Re-fetch the record inside the transaction ---
        GenerationRecord managedRecord = stateRepository.findById(record.getId());
        // ---------------------------------------------------

        // Modify the managed domain object
        managedRecord.setStatus(GenerationStatus.GENERATING);
        managedRecord.setUpdated(Instant.now());

        // Pass the managed domain object to the repository.
        stateRepository.update(managedRecord);
    }

    @Override
    @Transactional
    protected void reconcileGenerating(GenerationRecord record) {
        // --- FIX: Re-fetch the record inside the transaction ---
        GenerationRecord managedRecord = stateRepository.findById(record.getId());
        // ---------------------------------------------------

        if (managedRecord == null) {
            log.warn("Could not find record with ID '{}' to reconcile.", record.getId());
            return;
        }

        // Check if the simulated task has finished.
        if (!isFinished(managedRecord)) {
            log.debug("SyftGenerator: Job '{}' is still running.", managedRecord.getId());
            return;
        }

        log.info("SyftGenerator: Job '{}' has finished.", managedRecord.getId());

        if (isSuccessful(managedRecord)) {
            // Modify the managed domain object
            managedRecord.setStatus(GenerationStatus.FINISHED);
            managedRecord.setFinished(Instant.now());

            String fakeS3Url1 = String.format("s3://your-bucket/sboms/%s/%s.json", managedRecord.getId(), UUID.randomUUID());
            String fakeS3Url2 = String.format("s3://your-bucket/sboms/%s/%s.json", managedRecord.getId(), UUID.randomUUID());
            managedRecord.setSbomUrls(List.of(fakeS3Url1, fakeS3Url2));

            // Pass the modified domain object to the repository.
            stateRepository.update(managedRecord);

            // Publish the result.
            resultPublisher.publishSuccess(managedRecord);
        }
    }
}
