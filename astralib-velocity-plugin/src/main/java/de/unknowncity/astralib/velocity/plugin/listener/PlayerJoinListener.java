package de.unknowncity.astralib.velocity.plugin.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import de.unknowncity.astralib.velocity.plugin.AstraLibVelocityPlugin;

public class PlayerJoinListener {
    private final AstraLibVelocityPlugin plugin;

    public PlayerJoinListener(AstraLibVelocityPlugin plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onPostLogin(PostLoginEvent event) {
        var player = event.getPlayer();
        var uuid = player.getUniqueId();

        plugin.languageService().loadPlayerInCache(uuid);
        plugin.playerNameService().cachePlayer(uuid, player.getUsername());
    }
}
