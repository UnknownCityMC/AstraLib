package de.unknowncity.astralib.velocity.api.hook;

import de.unknowncity.astralib.common.hook.PluginHook;
import de.unknowncity.astralib.velocity.api.plugin.VelocityAstraPlugin;

public class VelocityPluginHook extends PluginHook<VelocityAstraPlugin> {

    public VelocityPluginHook(String identifier, VelocityAstraPlugin plugin) {
        super(identifier, plugin);
    }

    @Override
    public boolean isAvailable() {
        return plugin.server().getPluginManager().getPlugin(identifier).isPresent();
    }

    @Override
    public void initialize() {

    }
}
