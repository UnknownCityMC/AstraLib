package de.unknowncity.astralib.common.message;

import de.unknowncity.astralib.common.message.lang.Language;
import de.unknowncity.astralib.common.message.lang.Localization;
import de.unknowncity.astralib.common.service.AstraLanguageService;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.serialize.SerializationException;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Platform independent messenger implementation. Platforms only provide
 * the plugin name, the online players, the audience-to-player mapping and
 * optionally a placeholder hook (e.g. PlaceholderAPI on Paper).
 */
public abstract class AbstractMessenger<P extends Audience> implements Messenger<P> {
    protected final Localization localization;
    protected final Language defaultLanguage;
    protected final AstraLanguageService<P> languageService;
    protected final MiniMessage miniMessage;
    protected final int defaultFadeInDuration;
    protected final int defaultStayDuration;
    protected final int defaultFadeOutDuration;
    protected final Logger logger;

    protected AbstractMessenger(
            Localization localization,
            Language defaultLanguage,
            AstraLanguageService<P> languageService,
            int defaultFadeInDuration,
            int defaultStayDuration,
            int defaultFadeOutDuration,
            Logger logger
    ) {
        this.localization = localization;
        this.defaultLanguage = defaultLanguage;
        this.languageService = languageService;
        this.defaultFadeInDuration = defaultFadeInDuration;
        this.defaultStayDuration = defaultStayDuration;
        this.defaultFadeOutDuration = defaultFadeOutDuration;
        this.miniMessage = MiniMessage.miniMessage();
        this.logger = logger;
    }

    /**
     * The plugin name shown in not-available placeholders
     */
    protected abstract String pluginName();

    /**
     * All players currently online on this platform
     */
    protected abstract Collection<? extends P> onlinePlayers();

    /**
     * Maps a generic audience to a platform player if possible
     * @return the player or null if the audience is not a player
     */
    protected abstract P toPlayer(Audience audience);

    /**
     * Hook for platform placeholder frameworks (e.g. PlaceholderAPI)
     * @return the message with platform placeholders resolved
     */
    protected String resolvePlaceholders(P player, String messageString) {
        return messageString;
    }

    public Component prefix() {
        var messageString = getStringOrNotAvailable(defaultLanguage, NodePath.path("prefix"));
        return miniMessage.deserialize(messageString);
    }

    @Override
    public String notAvailable(NodePath path) {
        return "N/A (" + pluginName() + ") " + path;
    }

    @Override
    public String getStringOrNotAvailable(Language language, NodePath path) {
        return Objects.requireNonNullElseGet(getNullableString(language != null ? language : defaultLanguage, path), () -> notAvailable(path));
    }

    public String getStringOrNotAvailable(P player, NodePath path) {
        return getStringOrNotAvailable(languageService.getPlayerLanguage(player), path);
    }

    @Override
    public String getNullableString(Language language, NodePath path) {
        return localization.langNode(language != null ? language : defaultLanguage).node(path).getString();
    }

    public String getNullableString(P player, NodePath path) {
        var language = player == null ? defaultLanguage : languageService.getPlayerLanguage(player);
        return getNullableString(language, path);
    }

    @Override
    public List<String> getStringList(Language language, NodePath path) {
        List<String> list = null;
        try {
            list = localization.langNode(language != null ? language : defaultLanguage).node(path).getList(String.class);
        } catch (SerializationException e) {
            logger.severe(e.getMessage());
        }
        return list;
    }

    @Override
    public Component componentFromList(Language language, NodePath path, P player, TagResolver... resolvers) {
        var componentList = componentList(language != null ? language : defaultLanguage, path, player, resolvers);
        var component = Component.text();
        componentList.forEach(comp -> component.append(comp).appendNewline());

        return component.build();
    }

    @Override
    public Component componentFromList(Language language, NodePath path, TagResolver... resolvers) {
        return componentFromList(language != null ? language : defaultLanguage, path, null, resolvers);
    }

    public Component componentFromList(P player, NodePath path, TagResolver... resolvers) {
        return componentFromList(languageService.getPlayerLanguage(player), path, null, resolvers);
    }

    @Override
    public List<Component> componentList(Language language, NodePath path, P player, TagResolver... resolvers) {
        var messageStringList = getStringList(language, path);

        if (messageStringList == null || messageStringList.isEmpty()) {
            return List.of(Component.text(notAvailable(path)));
        }

        if (player != null) {
            messageStringList = messageStringList.stream().map(s -> resolvePlaceholders(player, s)).toList();
        }

        return messageStringList.stream().map(string -> miniMessage.deserialize(
                string,
                TagResolver.resolver(resolvers),
                TagResolver.resolver(Placeholder.component("prefix", prefix()))
        )).toList();
    }

    @Override
    public List<Component> componentList(Language language, NodePath path, TagResolver... resolvers) {
        return componentList(language, path, null, resolvers);
    }

    public List<Component> componentList(P player, NodePath path, TagResolver... resolvers) {
        return componentList(languageService.getPlayerLanguage(player), path, null, resolvers);
    }

    @Override
    public Component component(Language language, NodePath path, P player, TagResolver... resolvers) {
        if (localization.langNode(language != null ? language : defaultLanguage).node(path).isList()) {
            return componentFromList(language, path, player, resolvers);
        }

        var messageString = getStringOrNotAvailable(language != null ? language : defaultLanguage, path);

        return miniMessage.deserialize(
                resolvePlaceholders(player, messageString),
                TagResolver.resolver(resolvers),
                TagResolver.resolver(Placeholder.component("prefix", prefix()))
        );
    }

    @Override
    public Component component(Language language, NodePath path, TagResolver... resolvers) {
        return component(language, path, null, resolvers);
    }

    public Component component(P player, NodePath path, TagResolver... resolvers) {
        return component(languageService.getPlayerLanguage(player), path, null, resolvers);
    }

    @Override
    public void sendTitle(P player, NodePath pathTitle, NodePath pathSubTitle, Title.Times times, TagResolver... tagResolvers) {
        var language = languageService.getPlayerLanguage(player);

        var title = component(language, pathTitle, player, tagResolvers);
        var subtitle = component(language, pathSubTitle, player, tagResolvers);

        player.showTitle(Title.title(
                title, subtitle, times
        ));
    }

    @Override
    public void broadcastTitle(Collection<? extends P> players, NodePath pathTitle, NodePath pathSubTitle, Title.Times times, TagResolver... tagResolvers) {
        players.forEach(player -> sendTitle(player, pathTitle, pathSubTitle, times, tagResolvers));
    }

    @Override
    public void broadcastTitle(Collection<? extends P> players, NodePath pathTitle, NodePath pathSubTitle, TagResolver... tagResolvers) {
        broadcastTitle(players, pathTitle, pathSubTitle, Title.Times.times(Duration.ZERO, Duration.ofSeconds(3), Duration.ZERO), tagResolvers);
    }

    @Override
    public void broadcastTitle(NodePath pathTitle, NodePath pathSubTitle, Title.Times times, TagResolver... tagResolvers) {
        broadcastTitle(onlinePlayers(), pathTitle, pathSubTitle, times, tagResolvers);
    }

    @Override
    public void broadcastTitle(NodePath pathTitle, NodePath pathSubTitle, TagResolver... tagResolvers) {
        broadcastTitle(onlinePlayers(), pathTitle, pathSubTitle, tagResolvers);
    }

    @Override
    public void sendActionBar(P player, NodePath path, TagResolver... tagResolvers) {
        var language = languageService.getPlayerLanguage(player);

        var actionBar = component(language, path, player, tagResolvers);

        player.sendActionBar(actionBar);
    }

    @Override
    public void broadcastActionBar(Collection<? extends P> players, NodePath path, TagResolver... tagResolvers) {
        players.forEach(player -> sendActionBar(player, path, tagResolvers));
    }

    @Override
    public void broadcastActionBar(NodePath path, TagResolver... tagResolvers) {
        broadcastActionBar(onlinePlayers(), path, tagResolvers);
    }

    @Override
    public void sendMessage(Audience audience, NodePath path, TagResolver... tagResolvers) {
        var player = toPlayer(audience);
        var language = player != null ? languageService.getPlayerLanguage(player) : defaultLanguage;

        var message = component(language, path, player, tagResolvers);

        audience.sendMessage(message);
    }

    @Override
    public void broadcastMessage(Collection<? extends P> players, NodePath path, TagResolver... tagResolvers) {
        players.forEach(player -> sendMessage(player, path, tagResolvers));
    }

    @Override
    public void broadcastMessage(NodePath path, TagResolver... tagResolvers) {
        broadcastMessage(onlinePlayers(), path, tagResolvers);
    }
}
