package de.unknowncity.astralib.velocity.platform;

import com.velocitypowered.api.proxy.ProxyServer;
import de.unknowncity.astralib.common.platform.AstraPlatform;
import de.unknowncity.astralib.velocity.plugin.AstraLibVelocityPlugin;

import java.util.logging.Logger;

public class VelocityAstraPlatform implements AstraPlatform {
    private final AstraLibVelocityPlugin plugin;
    private final ProxyServer server;
    private final Logger logger = Logger.getLogger("AstraLib");

    public VelocityAstraPlatform(AstraLibVelocityPlugin plugin, ProxyServer server) {
        this.plugin = plugin;
        this.server = server;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public String getPlatformName() {
        return "Velocity";
    }

    // The proxy has no main thread, so sync and async tasks both run on the scheduler
    @Override
    public void runSync(Runnable runnable) {
        server.getScheduler().buildTask(plugin, runnable).schedule();
    }

    @Override
    public void runAsync(Runnable runnable) {
        server.getScheduler().buildTask(plugin, runnable).schedule();
    }
}
