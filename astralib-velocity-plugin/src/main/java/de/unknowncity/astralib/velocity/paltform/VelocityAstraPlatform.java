package de.unknowncity.astralib.velocity.paltform;

import com.velocitypowered.api.proxy.ProxyServer;
import de.unknowncity.astralib.common.platform.AstraPlatform;
import de.unknowncity.astralib.velocity.plugin.AstraLibVelocityPlugin;

import java.util.logging.Logger;

public class VelocityAstraPlatform implements AstraPlatform {
    private final AstraLibVelocityPlugin plugin;

    public VelocityAstraPlatform(AstraLibVelocityPlugin plugin, ProxyServer server) {
        this.plugin = plugin;
    }

    @Override
    public Logger getLogger() {
        return null;
    }

    @Override
    public String getPlatformName() {
        return "";
    }

    @Override
    public void runSync(Runnable runnable) {
        runnable.run();
    }

    @Override
    public void runAsync(Runnable runnable) {
        runnable.run();
    }
}
