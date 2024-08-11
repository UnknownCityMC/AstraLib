package de.unknowncity.astralib.paper.api.lib;

import de.unknowncity.astralib.paper.plugin.AstraLibPaperPlugin;

public class AstraLibPaper {
    private static AstraLibPaperPlugin plugin;


    public static AstraLibPaperPlugin getAstraLibPlugin() {
        return plugin;
    }

    public static void setAstraLibPlugin(AstraLibPaperPlugin astraLibPlugin) {
        plugin = astraLibPlugin;
    }
}
