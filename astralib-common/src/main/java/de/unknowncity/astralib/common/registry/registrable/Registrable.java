package de.unknowncity.astralib.common.registry.registrable;

public interface Registrable<I> {
    void startup(I plugin);
}
