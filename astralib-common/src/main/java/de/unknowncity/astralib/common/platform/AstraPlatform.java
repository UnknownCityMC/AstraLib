package de.unknowncity.astralib.common.platform;

import java.util.logging.Logger;

public interface AstraPlatform {
    Logger getLogger();
    String getPlatformName();
    void runSync(Runnable runnable);
    void runAsync(Runnable runnable);
}
