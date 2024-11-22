package de.unknowncity.astralib.paper.api.lib;

import de.unknowncity.astralib.common.service.AstraLanguageService;
import de.unknowncity.astralib.paper.api.message.PaperMessenger;
import org.bukkit.entity.Player;

public record AstraLibPaper(AstraLanguageService<Player> astraLanguageService, PaperMessenger paperMessenger) {
}
