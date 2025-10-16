package org.jboss.sbomer.adapter.et.core.domain.advisory;

/**
 * <p>
 * Represents an attached build in an advisory.
 * </p>
 * 
 * <p>
 * The identifier value is related to the type of the build. For container
 * images, it is the image digest (e.g.
 * "docker.io/org/repo@sha256:cf652def0cafa7d3fdd28500e1cd235783bfc240247034dd1bcf7a5ae5eda1ef").
 * </p>
 */
public record Build(Long id, String nvr, String type, String identifier) {
}
