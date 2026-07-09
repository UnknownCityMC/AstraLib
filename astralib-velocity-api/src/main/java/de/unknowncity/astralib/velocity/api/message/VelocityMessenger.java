package de.unknowncity.astralib.velocity.api.message;

import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.unknowncity.astralib.common.message.AbstractMessenger;
import de.unknowncity.astralib.common.message.lang.Language;
import de.unknowncity.astralib.common.message.lang.Localization;
import de.unknowncity.astralib.common.service.AstraLanguageService;
import de.unknowncity.astralib.common.service.FallbackLanguageService;
import net.kyori.adventure.audience.Audience;

import java.util.Collection;
import java.util.logging.Logger;

public class VelocityMessenger extends AbstractMessenger<Player> {
    private final PluginDescription pluginDescription;
    private final ProxyServer proxyServer;

    private VelocityMessenger(
            Localization localization,
            Language defaultLanguage,
            AstraLanguageService<Player> languageService,
            int defaultFadeInDuration,
            int defaultStayDuration,
            int defaultFadeOutDuration,
            PluginDescription pluginDescription,
            ProxyServer proxyServer,
            Logger logger
    ) {
        super(localization, defaultLanguage, languageService, defaultFadeInDuration, defaultStayDuration, defaultFadeOutDuration, logger);
        this.pluginDescription = pluginDescription;
        this.proxyServer = proxyServer;
    }

    public static Builder builder(Localization localization, PluginDescription pluginDescription, ProxyServer proxyServer) {
        return new VelocityMessenger.Builder(localization, pluginDescription, proxyServer);
    }

    @Override
    protected String pluginName() {
        return pluginDescription.getName().orElse(pluginDescription.getId());
    }

    @Override
    protected Collection<? extends Player> onlinePlayers() {
        return proxyServer.getAllPlayers();
    }

    @Override
    protected Player toPlayer(Audience audience) {
        return audience instanceof Player player ? player : null;
    }

    public static class Builder {
        public final Localization localization;
        private AstraLanguageService<Player> languageService = null;
        private Language defaultLanguage = Language.ENGLISH;
        private int defaultFadeInDuration = 0;
        private int defaultStayDuration = 1;
        private int defaultFadeOutDuration = 0;
        private final PluginDescription pluginDescription;
        private final ProxyServer proxyServer;
        private Logger logger = Logger.getLogger("VelocityMessenger");

        public Builder(Localization localization, PluginDescription pluginDescription, ProxyServer proxyServer) {
            this.localization = localization;
            this.pluginDescription = pluginDescription;
            this.proxyServer = proxyServer;
        }

        public Builder withDefaultLanguage(Language defaultLanguage) {
            Builder.this.defaultLanguage = defaultLanguage;
            return Builder.this;
        }

        public Builder withLanguageService(AstraLanguageService<Player> languageService) {
            Builder.this.languageService = languageService;
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

        public VelocityMessenger build() {
            if (languageService == null) {
                languageService = FallbackLanguageService.create(defaultLanguage);
            }
            return new VelocityMessenger(
                    localization,
                    defaultLanguage,
                    languageService,
                    defaultFadeInDuration,
                    defaultStayDuration,
                    defaultFadeOutDuration,
                    pluginDescription,
                    proxyServer,
                    logger
            );
        }
    }
}
