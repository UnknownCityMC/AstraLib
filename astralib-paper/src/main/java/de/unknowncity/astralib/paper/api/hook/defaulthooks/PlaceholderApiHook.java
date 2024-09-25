package de.unknowncity.astralib.paper.api.hook.defaulthooks;

import de.unknowncity.astralib.paper.api.hook.PaperPluginHook;
import org.bukkit.Server;

public class PlaceholderApiHook extends PaperPluginHook {
    public PlaceholderApiHook(Server server) {
        super("PlaceholderAPI", server);
    }
}
