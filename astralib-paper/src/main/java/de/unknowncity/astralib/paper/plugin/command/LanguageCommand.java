package de.unknowncity.astralib.paper.plugin.command;

import de.unknowncity.astralib.common.message.lang.Language;
import de.unknowncity.astralib.paper.api.command.PaperCommand;
import de.unknowncity.astralib.paper.api.command.sender.PaperCommandSource;
import de.unknowncity.astralib.paper.api.command.sender.PaperPlayerCommandSource;
import de.unknowncity.astralib.paper.plugin.AstraLibPaperPlugin;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
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
    public void apply(CommandManager<PaperCommandSource> commandManager) {
        commandManager.command(commandManager.commandBuilder("language")
                .senderType(PaperPlayerCommandSource.class)
                .required("language", StringParser.stringParser(), SuggestionProvider.suggestingStrings(plugin.localization().langIdentifiers()))
                .handler(this::handle)
        );

        commandManager.command(commandManager.commandBuilder("language")
                .senderType(PaperPlayerCommandSource.class)
                .handler(this::handleCurrent)
        );
    }

    private void handle(CommandContext<PaperPlayerCommandSource> commandContext) {
        var player = (Player) commandContext.sender().platformCommandSender();
        var languageString = (String) commandContext.get("language");

        if (!plugin.localization().isValidLanguage(languageString)) {
            plugin.messenger().sendMessage(player, NodePath.path("command", "language", "invalid-lang"), Placeholder.parsed("lang", languageString));
            return;
        }

        var language = new Language(languageString);

        plugin.languageService().setPlayerLanguage(player, language);
        plugin.messenger().sendMessage(player, NodePath.path("command", "language", "success"), Placeholder.parsed("lang", language.langIdentifier()));
    }

    private void handleCurrent(CommandContext<PaperPlayerCommandSource> commandContext) {
        var player = (Player) commandContext.sender().platformCommandSender();
        var language = plugin.languageService().getPlayerLanguage(player);
        plugin.messenger().sendMessage(player, NodePath.path("command", "language", "info"), Placeholder.parsed("lang", language.langIdentifier()));
    }

    @Override
    public void startup(AstraLibPaperPlugin plugin) {

    }
}
