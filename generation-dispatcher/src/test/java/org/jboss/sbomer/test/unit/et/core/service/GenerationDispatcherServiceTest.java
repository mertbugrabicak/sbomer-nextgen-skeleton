package org.jboss.sbomer.test.unit.et.core.service;

import static org.mockito.Mockito.verify;

import org.jboss.sbomer.dispatcher.core.port.spi.GenerationEventPublisher;
import org.jboss.sbomer.dispatcher.core.service.GenerationDispatcherService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GenerationDispatcherServiceTest {

    @InjectMocks
    private GenerationDispatcherService generationDispatcherService;

    @Mock
    private GenerationEventPublisher generationEventPublisher;

}
