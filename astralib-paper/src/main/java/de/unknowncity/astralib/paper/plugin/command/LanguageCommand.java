package de.unknowncity.astralib.paper.plugin.command;

import de.unknowncity.astralib.common.message.lang.Language;
import de.unknowncity.astralib.paper.api.command.PaperCommand;
import de.unknowncity.astralib.paper.api.command.sender.PaperCommandSource;
import de.unknowncity.astralib.paper.api.command.sender.PaperPlayerCommandSource;
import de.unknowncity.astralib.paper.plugin.AstraLibPaperPlugin;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.SuggestionProvider;
import org.spongepowered.configurate.NodePath;

public class LanguageCommand extends PaperCommand<AstraLibPaperPlugin> {

    public LanguageCommand(AstraLibPaperPlugin plugin) {
        super(plugin);
    }

    @Override
    public void apply(CommandManager<CommandSender> commandManager) {
        commandManager.command(commandManager.commandBuilder("language")
                .senderType(Player.class)
                .required("language", StringParser.stringParser(), SuggestionProvider.suggestingStrings(plugin.localization().langIdentifiers()))
                .handler(this::handle)
        );

        commandManager.command(commandManager.commandBuilder("language")
                .senderType(Player.class)
                .handler(this::handleCurrent)
        );
    }

    private void handle(CommandContext<Player> commandContext) {
        var player = commandContext.sender();
        var languageString = (String) commandContext.get("language");

        if (!plugin.localization().isValidLanguage(languageString)) {
            plugin.messenger().sendMessage(player, NodePath.path("command", "language", "invalid-lang"), Placeholder.parsed("lang", languageString));
            return;
        }

        var language = new Language(languageString);

        plugin.languageService().setPlayerLanguage(player, language);
        plugin.messenger().sendMessage(player, NodePath.path("command", "language", "success"), Placeholder.parsed("lang", language.langIdentifier()));
    }

    private void handleCurrent(CommandContext<Player> commandContext) {
        var player = commandContext.sender();
        var language = plugin.languageService().getPlayerLanguage(player);
        plugin.messenger().sendMessage(player, NodePath.path("command", "language", "info"), Placeholder.parsed("lang", language.langIdentifier()));
    }

    @Override
    public void startup(AstraLibPaperPlugin plugin) {

    }
}
