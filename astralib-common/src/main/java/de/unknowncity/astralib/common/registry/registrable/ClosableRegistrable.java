package de.unknowncity.astralib.common.registry.registrable;

public interface ClosableRegistrable<I> extends Registrable<I> {
    void shutdown();
}
