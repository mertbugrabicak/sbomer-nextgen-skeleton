package org.jboss.sbomer.adapter.et.core.domain.generation;

/**
 * TODO: Should type be an enum? Probably not, because it is controlled by
 * pluggable generators.
 * TODO: Can we do any sort of validation on the identifier based on the type? Should we?
 */
public record GenerationTarget(String type, String identifier) {

}
