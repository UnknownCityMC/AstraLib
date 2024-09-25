package de.unknowncity.astralib.common.service;

import de.unknowncity.astralib.common.message.lang.Language;

import java.util.UUID;

public class FallbackLanguageService<P> implements AstraLanguageService<P> {
    private final Language defaultLanguage;

    private FallbackLanguageService(Language defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public static <P> FallbackLanguageService<P> create(Language defaultLanguage) {
        return new FallbackLanguageService<>(defaultLanguage);
    }

    @Override
    public Language getDefaultLanguage() {
        return defaultLanguage;
    }

    public Language getPlayerLanguage(P player) {
        return defaultLanguage;
    }

    public void setPlayerLanguage(P player, Language language) {
        // Nothing to do, per player language is disabled in this plugin
    }

    @Override
    public boolean isLanguageSelected(P player) {
        return true;
    }

    public void loadPlayerInCache(UUID uuid) {
        // Nothing to do, per player language is disabled in this plugin
    }

    public void removePlayerFromCache(UUID uuid) {
        // Nothing to do, per player language is disabled in this plugin
    }
}