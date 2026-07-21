package de.unknowncity.astralib.velocity.plugin.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import de.unknowncity.astralib.velocity.plugin.AstraLibVelocityPlugin;

public class PlayerDisconnectListener {
    private final AstraLibVelocityPlugin plugin;

    public PlayerDisconnectListener(AstraLibVelocityPlugin plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        plugin.languageService().removePlayerFromCache(event.getPlayer().getUniqueId());
    }
}
