package de.unknowncity.astralib.velocity.api.lib;

import com.velocitypowered.api.proxy.Player;
import de.unknowncity.astralib.common.service.AstraLanguageService;
import de.unknowncity.astralib.common.service.PlayerNameService;
import de.unknowncity.astralib.velocity.api.message.VelocityMessenger;

public record AstraLibVelocity(
        AstraLanguageService<Player> astraLanguageService,
        VelocityMessenger velocityMessenger,
        PlayerNameService playerNameService
) {
}
