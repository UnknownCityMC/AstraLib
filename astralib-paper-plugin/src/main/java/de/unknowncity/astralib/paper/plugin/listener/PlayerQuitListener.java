package de.unknowncity.astralib.paper.plugin.listener;

import de.unknowncity.astralib.paper.plugin.AstraLibPaperPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private final AstraLibPaperPlugin plugin;

    public PlayerQuitListener(AstraLibPaperPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.languageService().removePlayerFromCache(event.getPlayer().getUniqueId());
    }
}
