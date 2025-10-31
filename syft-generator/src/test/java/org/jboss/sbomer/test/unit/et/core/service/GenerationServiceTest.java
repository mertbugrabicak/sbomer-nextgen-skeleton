package org.jboss.sbomer.test.unit.et.core.service;

import static org.mockito.Mockito.verify;

import org.jboss.sbomer.generator.core.port.spi.GenerationUpdateNotifier;
import org.jboss.sbomer.generator.core.service.GenerationService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GenerationServiceTest {

    @InjectMocks
    private GenerationService generationService;

    @Mock
    private GenerationUpdateNotifier generationUpdateNotifier;

}
