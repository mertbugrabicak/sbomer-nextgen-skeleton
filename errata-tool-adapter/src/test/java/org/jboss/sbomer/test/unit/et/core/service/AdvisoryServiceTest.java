package org.jboss.sbomer.test.unit.et.core.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.jboss.sbomer.adapter.et.core.domain.advisory.Advisory;
import org.jboss.sbomer.adapter.et.core.domain.advisory.Build;
import org.jboss.sbomer.adapter.et.core.port.spi.ErrataTool;
import org.jboss.sbomer.adapter.et.core.port.spi.GenerationRequestService;
import org.jboss.sbomer.adapter.et.core.service.AdvisoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AdvisoryServiceTest {
    @Mock
    private ErrataTool errataTool;

    @InjectMocks
    private AdvisoryService advisoryService;

    @Mock
    private GenerationRequestService generationRequestService;

    @Test
    void shouldFetchBuildList() {
        final String advisoryId = "12345";

        // Mock the behavior of errataTool to return a predefined list of builds
        when(errataTool.fetchBuilds(advisoryId))
                .thenReturn(List.of(new Build(3366231l, "cdi-api-2.0.2-15.el10", "RPM", "3366231")));

        when(errataTool.getInfo(advisoryId)).thenReturn(new Advisory(advisoryId, "QE", false));

        advisoryService.requestGenerations(advisoryId);

        // Ensure the fetchBuildsAttached method was called once with the correct
        // advisoryId
        verify(errataTool, times(1)).fetchBuilds(advisoryId);
    }
}
