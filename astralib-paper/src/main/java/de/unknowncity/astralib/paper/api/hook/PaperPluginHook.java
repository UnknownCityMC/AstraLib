package de.unknowncity.astralib.paper.api.hook;

import de.unknowncity.astralib.common.hook.PluginHook;
import de.unknowncity.astralib.paper.api.plugin.PaperAstraPlugin;
import org.bukkit.Server;

public class PaperPluginHook extends PluginHook<Server, PaperAstraPlugin> {

    public PaperPluginHook(String identifier, Server server) {
        super(identifier, server);
    }

    @Override
    public boolean isAvailable() {
        return server.getPluginManager().isPluginEnabled(identifier);
    }

    @Override
    public void startup(PaperAstraPlugin plugin) {

    }
}
