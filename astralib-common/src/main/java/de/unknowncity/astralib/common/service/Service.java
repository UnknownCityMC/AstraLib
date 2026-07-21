package de.unknowncity.astralib.common.service;

import de.unknowncity.astralib.common.registry.registrable.ClosableRegistrable;
import de.unknowncity.astralib.common.registry.registrable.StartableRegistrable;

/**
 * Contract for services managed by a {@link ServiceRegistry}.
 * startup() and shutdown() default to no-ops.
 */
public interface Service<I> extends StartableRegistrable<I>, ClosableRegistrable<I> {

}
