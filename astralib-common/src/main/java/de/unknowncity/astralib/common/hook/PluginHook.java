package de.unknowncity.astralib.common.hook;

import de.unknowncity.astralib.common.registry.registrable.Registrable;

public abstract class PluginHook<S, I> implements Registrable<I> {
    protected final String identifier;

    public PluginHook(String identifier) {
        this.identifier = identifier;
    }

    public abstract boolean isAvailable(S server);

    public String getIdentifier() {
        return identifier;
    }
}
