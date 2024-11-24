package de.unknowncity.astralib.common.plugin;

import java.nio.file.Path;

public interface AstraPlugin {

    void onPluginEnable();

    void onPluginDisable();

    void disableSelf();

    void saveDefaultResource(String from, Path to);
}
