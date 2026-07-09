package de.unknowncity.astralib.velocity.plugin.database.service;

import com.velocitypowered.api.proxy.Player;
import de.unknowncity.astralib.common.database.dao.language.LanguageDao;
import de.unknowncity.astralib.common.service.DatabaseLanguageService;
import de.unknowncity.astralib.velocity.plugin.configuration.AstraLibConfiguration;

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
