package de.unknowncity.astralib.paper.api.plugin;

import de.unknowncity.astralib.common.command.CommandRegistry;
import de.unknowncity.astralib.common.configuration.AstraConfigurationLoader;
import de.unknowncity.astralib.common.hook.HookRegistry;
import de.unknowncity.astralib.common.message.lang.Language;
import de.unknowncity.astralib.common.plugin.AstraPlugin;
import de.unknowncity.astralib.common.service.ServiceRegistry;
import de.unknowncity.astralib.paper.api.command.sender.PaperCommandSource;
import de.unknowncity.astralib.paper.api.command.sender.PaperPlayerCommandSource;
import de.unknowncity.astralib.paper.api.hook.PaperPluginHook;
import de.unknowncity.astralib.paper.api.message.PaperMessenger;
import de.unknowncity.astralib.paper.plugin.database.service.LanguageService;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.caption.CaptionProvider;
import org.incendo.cloud.caption.CaptionRegistry;
import org.incendo.cloud.caption.StandardCaptionKeys;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.NodePath;

import java.nio.file.Path;
import java.util.logging.Level;

public class PaperAstraPlugin extends JavaPlugin implements AstraPlugin<CommandSender, Player> {
    protected PaperCommandManager<PaperCommandSource> commandManager;
    protected ServiceRegistry<PaperAstraPlugin> serviceRegistry;
    protected CaptionRegistry<PaperCommandSource> captionRegistry;
    protected HookRegistry<Server, PaperAstraPlugin, PaperPluginHook> hookRegistry;
    protected CommandRegistry<CommandSender, Player, PaperAstraPlugin> commandRegistry;
    protected AstraConfigurationLoader configLoader;
    protected Language defaultLanguage = Language.GERMAN;

    public static final String ASTRA_LIB_MAIN_CONFIG_NAME = "astraconfig.yml";
    public static final Path ASTRA_LIB_MAIN_CONFIG_PATH = Path.of("AstraLib", ASTRA_LIB_MAIN_CONFIG_NAME);

    @Override
    public void onEnable() {
        this.configLoader = new AstraConfigurationLoader(getLogger());
        this.hookRegistry = new HookRegistry<>(this);
        this.serviceRegistry = new ServiceRegistry<>(this);

        onPluginEnable();
    }

    public void initializeCommandManager(PaperMessenger messenger, LanguageService languageService) {
        try {
            this.commandManager = PaperCommandManager.builder(SenderMapper.create(
                    commandSourceStack -> commandSourceStack.getSender() instanceof Player ?
                            new PaperPlayerCommandSource((Player) commandSourceStack.getSender(), commandSourceStack) :
                            new PaperCommandSource(commandSourceStack.getSender(), commandSourceStack),
                    paperCommandSource -> paperCommandSource.commandSourceStack()

                    ))
                    .executionCoordinator(ExecutionCoordinator.<PaperCommandSource>builder().build())
                    .buildOnEnable(this);

        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Failed to initialize command manager", e);
            disableSelf();
        }

        this.captionRegistry = commandManager.captionRegistry();

        captionRegistry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_CHAR,
                        sender -> messenger.getString(sender instanceof Player player ? languageService.getPlayerLanguage(player) : Language.ENGLISH,
                                NodePath.path("exception", "argument-parse", "char")))
        );
        captionRegistry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_COLOR,
                        sender -> messenger.getString(sender instanceof Player player ? languageService.getPlayerLanguage(player) : Language.ENGLISH,
                                NodePath.path("exception", "argument-parse", "color")))
        );
        captionRegistry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_DURATION,
                        sender -> messenger.getString(sender instanceof Player player ? languageService.getPlayerLanguage(player) : Language.ENGLISH,
                                NodePath.path("exception", "argument-parse", "duration")))
        );
        captionRegistry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_ENUM,
                        sender -> messenger.getString(sender instanceof Player player ? languageService.getPlayerLanguage(player) : Language.ENGLISH,
                                NodePath.path("exception", "argument-parse", "enum")))
        );
        captionRegistry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_NUMBER,
                        sender -> messenger.getString(sender instanceof Player player ? languageService.getPlayerLanguage(player) : Language.ENGLISH,
                                NodePath.path("exception", "argument-parse", "number")))
        );
        captionRegistry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_STRING,
                        sender -> messenger.getString(sender instanceof Player player ? languageService.getPlayerLanguage(player) : Language.ENGLISH,
                                NodePath.path("exception", "argument-parse", "string")))
        );
        captionRegistry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_REGEX,
                        sender -> messenger.getString(sender instanceof Player player ? languageService.getPlayerLanguage(player) : Language.ENGLISH,
                                NodePath.path("exception", "argument-parse", "regex")))
        );
        captionRegistry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_UUID,
                        sender -> messenger.getString(sender instanceof Player player ? languageService.getPlayerLanguage(player) : Language.ENGLISH,
                                NodePath.path("exception", "argument-parse", "uuid")))
        );
        captionRegistry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_BOOLEAN,
                        sender -> messenger.getString(sender instanceof Player player ? languageService.getPlayerLanguage(player) : Language.ENGLISH,
                                NodePath.path("exception", "argument-parse", "boolean")))
        );
    }

    @Override
    public @NotNull Path getDataPath() {
        return super.getDataPath();
    }

    @Override
    public void onDisable() {
        onPluginDisable();
    }

    @Override
    public void disableSelf() {
        this.getServer().getPluginManager().disablePlugin(this);
    }

    @Override
    public void saveDefaultResource(Path path) {
        if (getDataFolder().toPath().resolve(path).toFile().exists()) {
            return;
        }

        saveResource(path.toString(), false);
    }

    @Override
    public void onPluginEnable() {

    }

    @Override
    public void onPluginDisable() {

    }

    public CommandManager<PaperCommandSource> commandManager() {
        return commandManager;
    }
}