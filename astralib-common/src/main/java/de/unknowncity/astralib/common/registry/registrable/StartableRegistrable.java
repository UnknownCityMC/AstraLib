package de.unknowncity.astralib.common.registry.registrable;

public interface StartableRegistrable<I> extends Registrable<I> {
    default void startup() {}
}
