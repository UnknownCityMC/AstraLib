package de.unknowncity.astralib.common.hook;

import de.unknowncity.astralib.common.registry.Registry;

public class HookRegistry<S, I, H extends PluginHook<S, I>> extends Registry<I, H> {
    public HookRegistry(I plugin) {
        super(plugin);
    }
}
