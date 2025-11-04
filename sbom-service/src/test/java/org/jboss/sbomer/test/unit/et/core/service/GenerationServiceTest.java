package org.jboss.sbomer.test.unit.et.core.service;

import static org.mockito.Mockito.verify;

import org.jboss.sbomer.sbom.service.core.port.spi.generation.GenerationScheduler;
import org.jboss.sbomer.sbom.service.core.service.GenerationService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GenerationServiceTest {

    @InjectMocks
    private GenerationService generationDispatcherService;

    @Mock
    private GenerationScheduler generationScheduler;

}
