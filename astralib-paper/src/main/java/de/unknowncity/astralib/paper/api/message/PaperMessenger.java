package de.unknowncity.astralib.paper.api.message;

import de.unknowncity.astralib.common.service.AstraLanguageService;
import de.unknowncity.astralib.common.message.Messenger;
import de.unknowncity.astralib.common.message.lang.Language;
import de.unknowncity.astralib.common.message.lang.Localization;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Collection;
import java.util.List;

public class PaperMessenger implements Messenger<CommandSender, Player> {
    private final Localization localization;
    private final Language defaultLanguage;
    private final AstraLanguageService<Player> languageService;
    private final MiniMessage miniMessage;
    private final boolean papiAvailable;

    public PaperMessenger(Localization localization, Language defaultLanguage, AstraLanguageService<Player> languageService, boolean papiAvailable) {
        this.localization = localization;
        this.defaultLanguage = defaultLanguage;
        this.languageService = languageService;
        this.miniMessage = MiniMessage.miniMessage();
        this.papiAvailable = papiAvailable;
    }

    public Component prefix() {
        var messageString = getStringOrNotAvailable(defaultLanguage, NodePath.path("prefix"));
        return miniMessage.deserialize(messageString);
    }

    @Override
    public String notAvailable(NodePath path) {
        return "N/A " + path;
    }

    @Override
    public String getStringOrNotAvailable(Language language, NodePath path) {
        return getString(language, path) == null ? notAvailable(path) : getString(language, path);
    }

    @Override
    public String getString(Language language, NodePath path) {
        return localization.langNode(language).node(path).getString();
    }

    @Override
    public Component componentFromList(Language language, NodePath path, Player player, TagResolver... resolvers) {
        var componentList = componentList(language, path, player, resolvers);
        var component = Component.text();
        componentList.forEach(comp -> component.append(comp).appendNewline());

        return component.build();
    }

    @Override
    public Component componentFromList(Language language, NodePath path, TagResolver... resolvers) {
        return componentFromList(language, path, null, resolvers);
    }

    @Override
    public List<Component> componentList(Language language, NodePath path, Player player, TagResolver... resolvers) {
        List<String> messageStringList;
        try {
            messageStringList = localization.langNode(language).node(path).getList(String.class);
        } catch (SerializationException e) {
            return List.of(Component.text(notAvailable(path)));
        }

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

    @Override
    public Component component(Language language, NodePath path, Player player, TagResolver... resolvers) {
        var messageString = getStringOrNotAvailable(language, path);

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

    @Override
    public void sendTitle(Player player, NodePath pathTitle, NodePath pathSubTitle, Title.Times times, TagResolver... tagResolvers) {
        var language = languageService.getPlayerLanguage(player);

        var title = component(language, pathTitle, player, tagResolvers);
        var subtitle = component(language, pathTitle, player, tagResolvers);

        player.showTitle(Title.title(
                title, subtitle, times
        ));
    }

    @Override
    public void broadcastTitle(Collection<Player> players, NodePath pathTitle, NodePath pathSubTitle, Title.Times times, TagResolver... tagResolvers) {
        players.forEach(player -> sendTitle(player, pathTitle, pathSubTitle, times, tagResolvers));
    }

    @Override
    public void sendActionBar(Player player, NodePath path, TagResolver... tagResolvers) {
        var language = languageService.getPlayerLanguage(player);

        var actionBar = component(language, path, player, tagResolvers);

        player.sendActionBar(actionBar);
    }

    @Override
    public void broadcastActionBar(Collection<Player> players, NodePath path, TagResolver... tagResolvers) {
        players.forEach(player -> sendActionBar(player, path, tagResolvers));
    }

    @Override
    public void sendMessage(CommandSender commandSender, NodePath path, TagResolver... tagResolvers) {
        var language = commandSender instanceof Player player ? languageService.getPlayerLanguage(player) : defaultLanguage;

        var message = component(language, path, commandSender instanceof Player player ? player : null, tagResolvers);

        commandSender.sendMessage(message);
    }

    @Override
    public void broadcastMessage(Collection<Player> players, NodePath path, TagResolver... tagResolvers) {
        players.forEach(player -> sendMessage(player, path, tagResolvers));
    }
}