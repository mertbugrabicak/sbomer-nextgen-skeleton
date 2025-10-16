package org.jboss.sbomer.adapter.et.adapter.in;

import org.jboss.sbomer.adapter.et.core.port.api.AdvisoryHandler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Handler for processing advisory updates messages received via UMB.
 * 
 * TODO: Implement the UMB handling and triggering of advisory processing.
 */
@ApplicationScoped
public class UmbAdvisoryHandler {

    private AdvisoryHandler advisoryHandler;

    @Inject
    UmbAdvisoryHandler(AdvisoryHandler advisoryHandler) {
        this.advisoryHandler = advisoryHandler;
    }
}
