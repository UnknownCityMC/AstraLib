package de.unknowncity.astralib.paper.plugin.database.service;

import de.unknowncity.astralib.common.service.AstraLanguageService;
import de.unknowncity.astralib.common.message.lang.Language;
import de.unknowncity.astralib.paper.plugin.configuration.AstraLibConfig;
import de.unknowncity.astralib.paper.plugin.database.dao.language.LanguageDao;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LanguageService implements AstraLanguageService<Player> {

    private final LanguageDao languageDao;
    private final Map<UUID, Language> languageCache;
    private final AstraLibConfig configuration;

    public LanguageService(LanguageDao languageDao, AstraLibConfig configuration) {
        this.languageDao = languageDao;
        this.languageCache = new HashMap<>();
        this.configuration = configuration;
    }

    @Override
    public Language getDefaultLanguage() {
        return configuration.languageSetting().defaultLanguage();
    }

    public Language getPlayerLanguage(Player player) {
        var language = languageCache.get(player.getUniqueId());
        return language != null ? language : getDefaultLanguage();
    }

    public void setPlayerLanguage(Player player, Language language) {
        languageCache.put(player.getUniqueId(), language);
        languageDao.write(player.getUniqueId(), language);
    }

    @Override
    public boolean isLanguageSelected(Player player) {
        return false;
    }

    public void loadPlayerInCache(UUID uuid) {
        // Load language in cache
        languageDao.read(uuid).thenAcceptAsync(language -> {
            if (language.isPresent()) {
                languageCache.put(uuid, language.get());
            } else {
                var defaultLanguage = configuration.languageSetting().defaultLanguage();
                languageDao.write(uuid, defaultLanguage);
                languageCache.put(uuid, defaultLanguage);
            }
        });
    }

    public void removePlayerFromCache(UUID uuid) {
        languageCache.remove(uuid);
    }
}