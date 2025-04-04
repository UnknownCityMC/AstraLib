package de.unknowncity.astralib.common.hook;

import de.unknowncity.astralib.common.registry.Registry;

public class HookRegistry<I, H extends PluginHook<I>> extends Registry<I, H> {
    public HookRegistry(I plugin) {
        super(plugin);
    }

    /**
     * Registers a new hook to the registry to be accessed later on
     * @param registrable an instance of a plugin hook
     */
    @Override
    public <R extends H> void register(R registrable) {
        super.register(registrable);
        registrable.initialize();
    }

    /**
     * Checks if a specific hook is ready to be used
     * (Most of the time this means a required plugin is present)
     * @return is the hook is ready to be used
     */
    public boolean isAvailable(Class<? extends H> hook) {
        return getRegistered(hook).isAvailable();
    }
}
