package de.unknowncity.astralib.common.service;

import de.unknowncity.astralib.common.message.lang.Language;

public interface AstraLanguageService<P> {
    Language getDefaultLanguage();

    Language getPlayerLanguage(P player);

    void setPlayerLanguage(P player, Language language);

    boolean isLanguageSelected(P player);
}
