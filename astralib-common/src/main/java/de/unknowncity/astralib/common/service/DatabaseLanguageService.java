package de.unknowncity.astralib.common.service;

import de.unknowncity.astralib.common.database.dao.language.LanguageDao;
import de.unknowncity.astralib.common.message.lang.Language;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Platform independent language service backed by a {@link LanguageDao}
 * with an in-memory cache. Platforms only need to provide the players UUID.
 */
public abstract class DatabaseLanguageService<P> implements AstraLanguageService<P> {

    protected final LanguageDao languageDao;
    // Written from async dao callbacks and read from the platform threads
    protected final Map<UUID, Language> languageCache = new ConcurrentHashMap<>();
    protected final Supplier<Language> defaultLanguage;

    public DatabaseLanguageService(LanguageDao languageDao, Supplier<Language> defaultLanguage) {
        this.languageDao = languageDao;
        this.defaultLanguage = defaultLanguage;
    }

    /**
     * Extracts the unique id of a platform player
     * @param player the platform player
     * @return the players unique id
     */
    protected abstract UUID uuidOf(P player);

    @Override
    public Language getDefaultLanguage() {
        return defaultLanguage.get();
    }

    @Override
    public Language getPlayerLanguage(P player) {
        if (player == null) {
            return getDefaultLanguage();
        }
        var language = languageCache.get(uuidOf(player));
        return language != null ? language : getDefaultLanguage();
    }

    @Override
    public void setPlayerLanguage(P player, Language language) {
        var uuid = uuidOf(player);
        if (languageCache.containsKey(uuid)) {
            languageDao.update(uuid, language);
        } else {
            languageDao.write(uuid, language);
        }
        languageCache.put(uuid, language);
    }

    @Override
    public boolean isLanguageSelected(P player) {
        return player != null && languageCache.containsKey(uuidOf(player));
    }

    public void loadPlayerInCache(UUID uuid) {
        if (languageCache.containsKey(uuid)) {
            return;
        }
        languageDao.read(uuid).thenAcceptAsync(language -> {
            if (language.isPresent()) {
                languageCache.put(uuid, language.get());
            } else {
                var fallback = getDefaultLanguage();
                languageDao.write(uuid, fallback);
                languageCache.put(uuid, fallback);
            }
        });
    }

    public void removePlayerFromCache(UUID uuid) {
        languageCache.remove(uuid);
    }
}
