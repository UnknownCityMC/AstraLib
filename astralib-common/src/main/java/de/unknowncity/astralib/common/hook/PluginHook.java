package de.unknowncity.astralib.common.hook;

import de.unknowncity.astralib.common.registry.registrable.Registrable;

public abstract class PluginHook<I> implements Registrable<I> {
    protected final String identifier;
    protected final I plugin;

    public PluginHook(String identifier, I plugin) {
        this.identifier = identifier;
        this.plugin = plugin;
    }

    public abstract void initialize();
    public abstract boolean isAvailable();

    public String getIdentifier() {
        return identifier;
    }
}
