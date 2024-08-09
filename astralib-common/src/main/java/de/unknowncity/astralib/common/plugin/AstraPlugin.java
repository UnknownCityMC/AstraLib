package de.unknowncity.astralib.common.plugin;

import java.nio.file.Path;

public interface AstraPlugin<C, P> {

    void onPluginEnable();

    void onPluginDisable();

    void disableSelf();

    void saveDefaultResource(Path path);
}
