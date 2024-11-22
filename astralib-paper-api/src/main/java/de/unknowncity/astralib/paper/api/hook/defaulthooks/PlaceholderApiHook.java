package de.unknowncity.astralib.paper.api.hook.defaulthooks;

import de.unknowncity.astralib.paper.api.hook.PaperPluginHook;
import de.unknowncity.astralib.paper.api.plugin.PaperAstraPlugin;
import org.bukkit.Server;

public class PlaceholderApiHook extends PaperPluginHook {
    public PlaceholderApiHook(PaperAstraPlugin plugin) {
        super("PlaceholderAPI", plugin);
    }
}
