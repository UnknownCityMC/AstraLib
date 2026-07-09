package de.unknowncity.astralib.paper.plugin.database.service;

import de.unknowncity.astralib.common.database.dao.language.LanguageDao;
import de.unknowncity.astralib.common.service.DatabaseLanguageService;
import de.unknowncity.astralib.paper.plugin.configuration.AstraLibConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LanguageService extends DatabaseLanguageService<Player> {

    public LanguageService(LanguageDao languageDao, AstraLibConfiguration configuration) {
        super(languageDao, () -> configuration.language().defaultLanguage());
    }

    @Override
    protected UUID uuidOf(Player player) {
        return player.getUniqueId();
    }
}
