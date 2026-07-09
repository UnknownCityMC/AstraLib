package de.unknowncity.astralib.paper.api.message;

import de.unknowncity.astralib.common.message.AbstractMessenger;
import de.unknowncity.astralib.common.message.lang.Language;
import de.unknowncity.astralib.common.message.lang.Localization;
import de.unknowncity.astralib.common.service.AstraLanguageService;
import de.unknowncity.astralib.common.service.FallbackLanguageService;
import de.unknowncity.astralib.paper.api.hook.defaulthooks.PlaceholderApiHook;
import io.papermc.paper.plugin.configuration.PluginMeta;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.logging.Logger;

public class PaperMessenger extends AbstractMessenger<Player> {
    private final boolean papiAvailable;
    private final PluginMeta pluginMeta;

    private PaperMessenger(
            Localization localization,
            Language defaultLanguage,
            AstraLanguageService<Player> languageService,
            boolean papiAvailable,
            int defaultFadeInDuration,
            int defaultStayDuration,
            int defaultFadeOutDuration,
            PluginMeta pluginMeta,
            Logger logger
    ) {
        super(localization, defaultLanguage, languageService, defaultFadeInDuration, defaultStayDuration, defaultFadeOutDuration, logger);
        this.papiAvailable = papiAvailable;
        this.pluginMeta = pluginMeta;
    }

    public static Builder builder(Localization localization, PluginMeta pluginMeta) {
        return new PaperMessenger.Builder(localization, pluginMeta);
    }

    @Override
    protected String pluginName() {
        return pluginMeta.getName();
    }

    @Override
    protected Collection<? extends Player> onlinePlayers() {
        return Bukkit.getOnlinePlayers();
    }

    @Override
    protected Player toPlayer(Audience audience) {
        return audience instanceof Player player ? player : null;
    }

    @Override
    protected String resolvePlaceholders(Player player, String messageString) {
        return papiAvailable ? PlaceholderAPI.setPlaceholders(player, messageString) : messageString;
    }

    public static class Builder {
        public final Localization localization;
        private AstraLanguageService<Player> languageService = null;
        private Language defaultLanguage = Language.ENGLISH;
        private boolean papiAvailable = false;
        private int defaultFadeInDuration = 0;
        private int defaultStayDuration = 1;
        private int defaultFadeOutDuration = 0;
        private PluginMeta pluginMeta;
        private Logger logger = Logger.getLogger("PaperMessenger");

        public Builder(Localization localization, PluginMeta pluginMeta) {
            this.localization = localization;
            this.pluginMeta = pluginMeta;
        }

        public Builder withDefaultLanguage(Language defaultLanguage) {
            Builder.this.defaultLanguage = defaultLanguage;
            return Builder.this;
        }

        public Builder withLanguageService(AstraLanguageService<Player> languageService) {
            Builder.this.languageService = languageService;
            return Builder.this;
        }

        public Builder withPlaceHolderAPI(PlaceholderApiHook placeholderApiHook) {
            Builder.this.papiAvailable = placeholderApiHook.isAvailable();
            return Builder.this;
        }

        public Builder withDefaultFadeInDuration(int defaultFadeInDuration) {
            Builder.this.defaultFadeInDuration = defaultFadeInDuration;
            return Builder.this;
        }

        public Builder withDefaultStayDuration(int defaultStayDuration) {
            Builder.this.defaultStayDuration = defaultStayDuration;
            return Builder.this;
        }

        public Builder withDefaultFadeOutDuration(int defaultFadeOutDuration) {
            Builder.this.defaultFadeOutDuration = defaultFadeOutDuration;
            return Builder.this;
        }

        public Builder withLogger(Logger logger) {
            Builder.this.logger = logger;
            return Builder.this;
        }

        public PaperMessenger build() {
            if (languageService == null) {
                languageService = FallbackLanguageService.create(defaultLanguage);
            }
            return new PaperMessenger(
                    localization,
                    defaultLanguage,
                    languageService,
                    papiAvailable,
                    defaultFadeInDuration,
                    defaultStayDuration,
                    defaultFadeOutDuration,
                    pluginMeta,
                    logger
            );
        }
    }
}
