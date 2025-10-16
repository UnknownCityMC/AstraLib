package de.unknowncity.astralib.paper.platform;

import de.unknowncity.astralib.common.platform.AstraPlatform;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class PaperAstraPlatform implements AstraPlatform {
    private final JavaPlugin plugin;

    public PaperAstraPlatform(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Logger getLogger() {
        return plugin.getLogger();
    }

    @Override
    public String getPlatformName() {
        return "Paper";
    }

    @Override
    public void runSync(Runnable runnable) {
        plugin.getServer().getScheduler().runTask(plugin, runnable);
    }

    @Override
    public void runAsync(Runnable runnable) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
    }
}
