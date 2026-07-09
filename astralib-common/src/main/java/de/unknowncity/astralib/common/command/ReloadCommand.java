package de.unknowncity.astralib.common.command;

import de.unknowncity.astralib.common.message.AbstractMessenger;
import de.unknowncity.astralib.common.platform.AstraLib;
import de.unknowncity.astralib.common.plugin.ReloadManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.SuggestionProvider;
import org.spongepowered.configurate.NodePath;

import java.util.logging.Level;

/**
 * The /astralib reload [plugin] command. Reloads any plugin registered
 * in the {@link ReloadManager}. Platform independent, registered by the
 * AstraLib platform plugins on their own command manager.
 */
public final class ReloadCommand {

    private ReloadCommand() {

    }

    public static <C extends Audience> void register(CommandManager<C> commandManager, AbstractMessenger<?> messenger) {
        commandManager.command(commandManager.commandBuilder("astralib")
                .literal("reload")
                .permission("astralib.command.reload")
                .required("plugin", StringParser.stringParser(),
                        SuggestionProvider.blockingStrings((context, input) -> ReloadManager.registeredPluginNames()))
                .handler(context -> {
                    String pluginName = context.get("plugin");
                    var sender = context.sender();
                    var pluginPlaceholder = Placeholder.parsed("plugin", pluginName);

                    if (!ReloadManager.isRegistered(pluginName)) {
                        messenger.sendMessage(sender, NodePath.path("command", "reload", "unknown-plugin"), pluginPlaceholder);
                        return;
                    }

                    try {
                        ReloadManager.reload(pluginName);
                        messenger.sendMessage(sender, NodePath.path("command", "reload", "success"), pluginPlaceholder);
                    } catch (Exception e) {
                        AstraLib.getPlatform().getLogger().log(Level.SEVERE, "Failed to reload plugin " + pluginName, e);
                        messenger.sendMessage(sender, NodePath.path("command", "reload", "error"), pluginPlaceholder);
                    }
                })
        );
    }
}
