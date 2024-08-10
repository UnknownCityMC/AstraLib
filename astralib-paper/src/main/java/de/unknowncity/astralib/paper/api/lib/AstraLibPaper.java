package de.unknowncity.astralib.paper.api.lib;

import de.unknowncity.astralib.paper.api.plugin.PaperAstraPlugin;

public class AstraLibPaper {
    private static PaperAstraPlugin plugin;


    public static PaperAstraPlugin getAstraLibPlugin() {
        return plugin;
    }

    public static void setAstraLibPlugin(PaperAstraPlugin astraLibPlugin) {
        plugin = astraLibPlugin;
    }
}
