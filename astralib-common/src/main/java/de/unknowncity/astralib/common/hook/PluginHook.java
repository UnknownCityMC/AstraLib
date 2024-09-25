package de.unknowncity.astralib.common.hook;

import de.unknowncity.astralib.common.registry.registrable.Registrable;

public abstract class PluginHook<S, I> implements Registrable<I> {
    protected final String identifier;
    protected final S server;

    public PluginHook(String identifier, S server) {
        this.identifier = identifier;
        this.server = server;
    }

    public abstract boolean isAvailable();

    public String getIdentifier() {
        return identifier;
    }
}
