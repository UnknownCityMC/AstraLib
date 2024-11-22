package de.unknowncity.astralib.paper.api.hook;

import de.unknowncity.astralib.common.hook.PluginHook;
import de.unknowncity.astralib.paper.api.plugin.PaperAstraPlugin;
import org.bukkit.Server;

public class PaperPluginHook extends PluginHook<PaperAstraPlugin> {

    public PaperPluginHook(String identifier, PaperAstraPlugin plugin) {
        super(identifier, plugin);
    }

    @Override
    public boolean isAvailable() {
        return plugin.getServer().getPluginManager().isPluginEnabled(identifier);
    }

    @Override
    public void initialize() {

    }
}
