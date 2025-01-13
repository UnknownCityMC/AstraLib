package de.unknowncity.astralib.paper.api.message;

import de.unknowncity.astralib.common.service.AstraLanguageService;
import de.unknowncity.astralib.common.message.Messenger;
import de.unknowncity.astralib.common.message.lang.Language;
import de.unknowncity.astralib.common.message.lang.Localization;
import de.unknowncity.astralib.common.service.FallbackLanguageService;
import de.unknowncity.astralib.paper.api.hook.defaulthooks.PlaceholderApiHook;
import io.papermc.paper.plugin.configuration.PluginMeta;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.serialize.SerializationException;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class PaperMessenger implements Messenger<Player> {
    private final Localization localization;
    private final Language defaultLanguage;
    private final AstraLanguageService<Player> languageService;
    private final MiniMessage miniMessage;
    private final boolean papiAvailable;
    private final int defaultFadeInDuration ;
    private final int defaultStayDuration;
    private final int defaultFadeOutDuration;
    private PluginMeta pluginMeta;

    private PaperMessenger(
            Localization localization,
            Language defaultLanguage,
            AstraLanguageService<Player> languageService,
            boolean papiAvailable,
            int defaultFadeInDuration,
            int defaultStayDuration,
            int defaultFadeOutDuration,
            PluginMeta pluginMeta
    ) {
        this.localization = localization;
        this.defaultLanguage = defaultLanguage;
        this.languageService = languageService;
        this.defaultFadeInDuration = defaultFadeInDuration;
        this.defaultStayDuration = defaultStayDuration;
        this.defaultFadeOutDuration = defaultFadeOutDuration;
        this.miniMessage = MiniMessage.miniMessage();
        this.papiAvailable = papiAvailable;
        this.pluginMeta = pluginMeta;
    }

    public static Builder builder(Localization localization, PluginMeta pluginMeta) {
        return new PaperMessenger.Builder(localization, pluginMeta);
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
                    pluginMeta
            );
        }
    }

    public Component prefix() {
        var messageString = getStringOrNotAvailable(defaultLanguage, NodePath.path("prefix"));
        return miniMessage.deserialize(messageString);
    }

    @Override
    public String notAvailable(NodePath path) {
        return "N/A (" + pluginMeta.getName() + ") " + path;
    }

    @Override
    public String getStringOrNotAvailable(Language language, NodePath path) {
        return Objects.requireNonNullElseGet(getNullableString(language != null ? language : defaultLanguage, path), () ->  notAvailable(path));
    }

    public String getStringOrNotAvailable(Player player, NodePath path) {
        return getStringOrNotAvailable(languageService.getPlayerLanguage(player), path);
    }

    @Override
    public String getNullableString(Language language, NodePath path) {
        return localization.langNode(language != null ? language : defaultLanguage).node(path).getString();
    }

    public String getNullableString(Player player, NodePath path) {
        var language = player == null ? defaultLanguage : languageService.getPlayerLanguage(player);
        return getNullableString(language, path);
    }

    @Override
    public List<String> getStringList(Language language, NodePath path) {
        List<String> list;
        try {
            list = localization.langNode(language != null ? language : defaultLanguage).node(path).getList(String.class);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public Component componentFromList(Language language, NodePath path, Player player, TagResolver... resolvers) {
        var componentList = componentList(language != null ? language : defaultLanguage, path, player, resolvers);
        var component = Component.text();
        componentList.forEach(comp -> component.append(comp).appendNewline());

        return component.build();
    }

    @Override
    public Component componentFromList(Language language, NodePath path, TagResolver... resolvers) {
        return componentFromList(language != null ? language : defaultLanguage , path, null, resolvers);
    }

    public Component componentFromList(Player player, NodePath path, TagResolver... resolvers) {
        return componentFromList(languageService.getPlayerLanguage(player), path, null, resolvers);
    }

    @Override
    public List<Component> componentList(Language language, NodePath path, Player player, TagResolver... resolvers) {
        var messageStringList = getStringList(language, path);

        if (messageStringList == null || messageStringList.isEmpty()) {
            return List.of(Component.text(notAvailable(path)));
        }

        if (papiAvailable && player != null) {
            messageStringList = messageStringList.stream().map(s -> PlaceholderAPI.setPlaceholders(player, s)).toList();
        }

        return messageStringList.stream().map(string -> miniMessage.deserialize(string, resolvers)).toList();
    }

    @Override
    public List<Component> componentList(Language language, NodePath path, TagResolver... resolvers) {
        return componentList(language, path, null, resolvers);
    }

    public List<Component> componentList(Player player, NodePath path, TagResolver... resolvers) {
        return componentList(languageService.getPlayerLanguage(player), path, null, resolvers);
    }

    @Override
    public Component component(Language language, NodePath path, Player player, TagResolver... resolvers) {
        if (localization.langNode(language != null ? language : defaultLanguage).isList()) {
            return componentFromList(language, path, player, resolvers);
        }

        var messageString = getStringOrNotAvailable(language != null ? language : defaultLanguage, path);

        var papiParsedString = papiAvailable && player != null ? PlaceholderAPI.setPlaceholders(player, messageString) : messageString;
        return miniMessage.deserialize(
                papiParsedString,
                TagResolver.resolver(resolvers),
                TagResolver.resolver(Placeholder.component("prefix", prefix()))
        );
    }

    @Override
    public Component component(Language language, NodePath path, TagResolver... resolvers) {
        return component(language, path, null, resolvers);
    }

    public Component component(Player player, NodePath path, TagResolver... resolvers) {
        return component(languageService.getPlayerLanguage(player), path, null, resolvers);
    }


    @Override
    public void sendTitle(Player player, NodePath pathTitle, NodePath pathSubTitle, Title.Times times, TagResolver... tagResolvers) {
        var language = languageService.getPlayerLanguage(player);

        var title = component(language, pathTitle, player, tagResolvers);
        var subtitle = component(language, pathSubTitle, player, tagResolvers);

        player.showTitle(Title.title(
                title, subtitle, times
        ));
    }

    @Override
    public void broadcastTitle(Collection<? extends Player> players, NodePath pathTitle, NodePath pathSubTitle, Title.Times times, TagResolver... tagResolvers) {
        players.forEach(player -> sendTitle(player, pathTitle, pathSubTitle, times, tagResolvers));
    }

    @Override
    public void broadcastTitle(Collection<? extends Player> players, NodePath pathTitle, NodePath pathSubTitle, TagResolver... tagResolvers) {
        broadcastTitle(players, pathTitle, pathSubTitle, Title.Times.times(Duration.ZERO, Duration.ofSeconds(3), Duration.ZERO), tagResolvers);
    }

    @Override
    public void broadcastTitle(NodePath pathTitle, NodePath pathSubTitle, Title.Times times, TagResolver... tagResolvers) {
        broadcastTitle(Bukkit.getOnlinePlayers(), pathTitle, pathSubTitle, times, tagResolvers);
    }

    @Override
    public void broadcastTitle(NodePath pathTitle, NodePath pathSubTitle, TagResolver... tagResolvers) {
        broadcastTitle(Bukkit.getOnlinePlayers(), pathTitle, pathSubTitle, tagResolvers);
    }

    @Override
    public void sendActionBar(Player player, NodePath path, TagResolver... tagResolvers) {
        var language = languageService.getPlayerLanguage(player);

        var actionBar = component(language, path, player, tagResolvers);

        player.sendActionBar(actionBar);
    }

    @Override
    public void broadcastActionBar(Collection<? extends Player> players, NodePath path, TagResolver... tagResolvers) {
        players.forEach(player -> sendActionBar(player, path, tagResolvers));
    }

    @Override
    public void broadcastActionBar(NodePath path, TagResolver... tagResolvers) {
        broadcastActionBar(Bukkit.getOnlinePlayers(), path, tagResolvers);
    }

    @Override
    public void sendMessage(Audience audience, NodePath path, TagResolver... tagResolvers) {
        var language = audience instanceof Player player ? languageService.getPlayerLanguage(player) : defaultLanguage;

        var message = component(language, path, audience instanceof Player player ? player : null, tagResolvers);

        audience.sendMessage(message);
    }

    @Override
    public void broadcastMessage(Collection<? extends Player> players, NodePath path, TagResolver... tagResolvers) {
        players.forEach(player -> sendMessage(player, path, tagResolvers));
    }

    @Override
    public void broadcastMessage(NodePath path, TagResolver... tagResolvers) {
        broadcastMessage(Bukkit.getOnlinePlayers(), path, tagResolvers);
    }
}