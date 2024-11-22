package de.unknowncity.astralib.paper.plugin.listener;

import de.unknowncity.astralib.paper.plugin.AstraLibPaperPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final AstraLibPaperPlugin plugin;

    public PlayerJoinListener(AstraLibPaperPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var player =  event.getPlayer();
        var uuid = player.getUniqueId();

        plugin.languageService().loadPlayerInCache(uuid);
    }
}
