package de.unknowncity.astralib.common.message;

import de.unknowncity.astralib.common.hook.PluginHook;
import de.unknowncity.astralib.common.message.lang.Language;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import org.spongepowered.configurate.NodePath;

import java.util.Collection;
import java.util.List;

public interface Messenger<C, P> {

    /**
     * Retrieves the not available string for a language string
     * @param path The path to the language string in the language configuration
     * @return A not available string
     */
     String notAvailable(NodePath path);

    /**
     * Retrieves a plain language string from the lang configuration
     * or if not available a dummy string stating the absence of the config
     * entry and the path where the entry should be located
     * @param language The language to find the component in
     * @param path The path to the language string in the language configuration
     * @return A plain language string or a dummy
     */
     String getStringOrNotAvailable(Language language, NodePath path);

    /**
     * Retrieves a language string as is
     * @param language The language to find the component in
     * @param path The path to the language string in the language configuration
     * @return A plain language string
     */
     String getString(Language language, NodePath path);

    /**
     * Retrieves a single component from multiple lines in the lang configuration
     * @param path The path to the language string in the language configuration
     * @param player The player that the placeholders should be replaced for
     * @param resolvers Optional placeholders and their replacements
     * @return A minimessage deserialized language component
     */
     Component componentFromList(Language language, NodePath path, P player, TagResolver... resolvers);

    /**
     * Retrieves a single component from multiple lines in the lang configuration
     * @param language The language to find the component in
     * @param path The path to the language string in the language configuration
     * @param resolvers Optional placeholders and their replacements
     * @return A minimessage deserialized language component
     */
    Component componentFromList(Language language, NodePath path, TagResolver... resolvers);

    /**
     * Retrieves a list component from the lang configuration
     * @param language The language to find the component in
     * @param path The path to the language string in the language configuration
     * @param player The player that the placeholders should be replaced for
     * @param resolvers Optional placeholders and their replacements
     * @return A minimessage deserialized language component
     */
     List<Component> componentList(Language language, NodePath path, P player, TagResolver... resolvers);

    /**
     * Retrieves a list component from the lang configuration
     * @param language The language to find the component in
     * @param path The path to the language string in the language configuration
     * @param resolvers Optional placeholders and their replacements
     * @return A minimessage deserialized language component
     */
    List<Component> componentList(Language language, NodePath path, TagResolver... resolvers);

    /**
     * Retrieves a component from the lang configuration
     * @param language The language to find the component in
     * @param path The path to the language string in the language configuration
     * @param player The player that the placeholders should be replaced for
     * @param resolvers Optional placeholders and their replacements
     * @return A minimessage deserialized language component
     */
    Component component(Language language, NodePath path, P player, TagResolver... resolvers);

    /**
     * Retrieves a component from the lang configuration
     * @param language The language to find the component in
     * @param path The path to the language string in the language configuration
     * @param resolvers Optional placeholders and their replacements
     * @return A minimessage deserialized language component
     */
     Component component(Language language, NodePath path, TagResolver... resolvers);

    /**
     * Sends a title and subtitle to the given player
     * @param player The player to send the title to
     * @param pathTitle The path to the title language string in the language configuration
     * @param pathSubTitle The path to the subtitle language string in the language configuration
     * @param times how long the title should fade in / out and stay
     * @param tagResolvers Optional placeholders and their replacements
     */
     void sendTitle(P player, NodePath pathTitle, NodePath pathSubTitle, Title.Times times, TagResolver... tagResolvers);


    /**
     * Sends a title and subtitle to the all online players
     * @param pathTitle The path to the title language string in the language configuration
     * @param pathSubTitle The path to the subtitle language string in the language configuration
     * @param times how long the title should fade in / out and stay
     * @param tagResolvers Optional placeholders and their replacements
     */
     void broadcastTitle(Collection<P> players, NodePath pathTitle, NodePath pathSubTitle, Title.Times times, TagResolver... tagResolvers);

    /**
     * Sends an action bar message and subtitle to the given player
     * @param player The player to send the title to
     * @param path The path to the title language string in the language configuration
     * @param tagResolvers Optional placeholders and their replacements
     */
     void sendActionBar(P player, NodePath path, TagResolver... tagResolvers);

    /**
     * Sends an action bar message and subtitle to all online players
     * @param path The path to the title language string in the language configuration
     * @param tagResolvers Optional placeholders and their replacements
     */
     void broadcastActionBar(Collection<P> players, NodePath path, TagResolver... tagResolvers);


    /**
     * Sends a message and subtitle to the given command sender
     * @param commandSender The command sender to send the message to
     * @param path The path to the title language string in the language configuration
     * @param tagResolvers Optional placeholders and their replacements
     */
    void sendMessage(C commandSender, NodePath path, TagResolver... tagResolvers);

    /**
     * Sends a message and subtitle to all online players
     * @param path The path to the title language string in the language configuration
     * @param tagResolvers Optional placeholders and their replacements
     */
     void broadcastMessage(Collection<P> players, NodePath path, TagResolver... tagResolvers);
}
