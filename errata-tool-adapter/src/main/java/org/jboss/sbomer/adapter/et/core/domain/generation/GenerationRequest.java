package org.jboss.sbomer.adapter.et.core.domain.generation;

import org.jboss.sbomer.adapter.et.core.domain.advisory.Build;

import com.github.f4b6a3.tsid.TsidCreator;

/**
 * <p>
 * <strong>WARNING</strong>: make sure that you have set following environment
 * variables in production to avoid clashes:
 * 
 * <ul>
 * <li>{@code TSIDCREATOR_NODE} - set it to a unique value per instance</li>
 * <li>{@code TSIDCREATOR_NODE_COUNT} - set it to the total number of
 * instances</li>
 * </ul>
 * </p>
 * 
 * TODO: Probably temporary class until we figure out how to share schema
 * across services
 * 
 * @see https://github.com/f4b6a3/tsid-creator/?tab=readme-ov-file#node-identifier
 * @return
 */
public record GenerationRequest(String id, GenerationTarget target) {

    public static GenerationRequest fromBuild(Build build) {
        return new GenerationRequest("G" + TsidCreator.getTsid1024().toString(),
                new GenerationTarget(build.type(), build.identifier()));
    }

}
