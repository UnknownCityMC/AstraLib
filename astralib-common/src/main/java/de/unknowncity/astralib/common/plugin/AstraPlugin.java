package de.unknowncity.astralib.common.plugin;

import java.nio.file.Path;

public interface AstraPlugin {

    void onPluginEnable();

    void onPluginDisable();

    /**
     * Called when the plugin gets reloaded via /astralib reload [plugin]
     * Override to reload configurations, language files etc.
     */
    void onPluginReload();

    void disableSelf();

    void saveDefaultResource(String from, Path to);
}
