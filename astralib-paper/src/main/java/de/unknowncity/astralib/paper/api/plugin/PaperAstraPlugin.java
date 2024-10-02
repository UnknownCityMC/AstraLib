package de.unknowncity.astralib.paper.api.plugin;

import de.unknowncity.astralib.common.command.CommandRegistry;
import de.unknowncity.astralib.common.configuration.AstraConfigurationLoader;
import de.unknowncity.astralib.common.hook.HookRegistry;
import de.unknowncity.astralib.common.message.lang.Language;
import de.unknowncity.astralib.common.plugin.AstraPlugin;
import de.unknowncity.astralib.common.service.AstraLanguageService;
import de.unknowncity.astralib.common.service.ServiceRegistry;
import de.unknowncity.astralib.paper.api.command.sender.PaperCommandSource;
import de.unknowncity.astralib.paper.api.command.sender.PaperPlayerCommandSource;
import de.unknowncity.astralib.paper.api.hook.PaperPluginHook;
import de.unknowncity.astralib.paper.api.hook.defaulthooks.PlaceholderApiHook;
import de.unknowncity.astralib.paper.api.message.PaperMessenger;
import de.unknowncity.astralib.paper.plugin.AstraLibPaperPlugin;
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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.NodePath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

public class PaperAstraPlugin extends JavaPlugin implements AstraPlugin {
    protected PaperCommandManager<PaperCommandSource> commandManager;
    protected CommandRegistry<CommandSender, Player, PaperAstraPlugin> commandRegistry;

    protected ServiceRegistry<PaperAstraPlugin> serviceRegistry;
    protected CaptionRegistry<PaperCommandSource> captionRegistry;
    protected HookRegistry<Server, PaperAstraPlugin, PaperPluginHook> hookRegistry;

    protected AstraLanguageService<Player> languageService;
    protected AstraConfigurationLoader configLoader;
    protected Language defaultLanguage = Language.GERMAN;

    public static final String ASTRA_LIB_MAIN_CONFIG_NAME = "astraconfig.yml";
    public static final Path ASTRA_LIB_MAIN_CONFIG_PATH = Path.of("AstraLib", ASTRA_LIB_MAIN_CONFIG_NAME);

    @Override
    public void onEnable() {
        this.hookRegistry = new HookRegistry<>(this);

        this.configLoader = new AstraConfigurationLoader(this.getLogger());
        registerDefaultHooks();

        this.serviceRegistry = new ServiceRegistry<>(this);

        var astraLibPaperPlugin = getPlugin(AstraLibPaperPlugin.class);
        this.languageService = astraLibPaperPlugin.languageService();

        initializeCommandManager(astraLibPaperPlugin.messenger());

        // Plugin exclusive logic starts here
        onPluginEnable();
    }

    @ApiStatus.Internal
    public void initializeCommandManager(PaperMessenger messenger) {
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
                        sender ->  messenger.getString(sender instanceof Player player ? player : null,
                                NodePath.path("exception", "argument-parse", "char")))
        );
        captionRegistry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_COLOR,
                        sender -> messenger.getString(sender instanceof Player player ? player : null,
                                NodePath.path("exception", "argument-parse", "color")))
        );
        captionRegistry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_DURATION,
                        sender -> messenger.getString(sender instanceof Player player ? player : null,
                                NodePath.path("exception", "argument-parse", "duration")))
        );
        captionRegistry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_ENUM,
                        sender -> messenger.getString(sender instanceof Player player ? player : null,
                                NodePath.path("exception", "argument-parse", "enum")))
        );
        captionRegistry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_NUMBER,
                        sender -> messenger.getString(sender instanceof Player player ? player : null,
                                NodePath.path("exception", "argument-parse", "number")))
        );
        captionRegistry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_STRING,
                        sender -> messenger.getString(sender instanceof Player player ? player : null,
                                NodePath.path("exception", "argument-parse", "string")))
        );
        captionRegistry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_REGEX,
                        sender -> messenger.getString(sender instanceof Player player ? player : null,
                                NodePath.path("exception", "argument-parse", "regex")))
        );
        captionRegistry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_UUID,
                        sender -> messenger.getString(sender instanceof Player player ? player : null,
                                NodePath.path("exception", "argument-parse", "uuid")))
        );
        captionRegistry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_BOOLEAN,
                        sender -> messenger.getString(sender instanceof Player player ? player : null,
                                NodePath.path("exception", "argument-parse", "boolean")))
        );
    }

    private void registerDefaultHooks() {
        hookRegistry.register(new PlaceholderApiHook(getServer()));
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
    public void saveDefaultResource(Path from, Path to) {
        if (Files.exists(to)) {
            return;
        }

        try (var resourceAsStream = getClass().getResourceAsStream("/" + from.toString())) {
            if (resourceAsStream == null) {
                getLogger().log(
                        Level.SEVERE, "Failed to save " + from + ". " +
                                "The plugin developer tried to save a file that does not exist in the plugins jar file!"
                );
                return;
            }
            Files.createDirectories(to.getParent());
            try (var outputStream = Files.newOutputStream(to)) {
                resourceAsStream.transferTo(outputStream);
            }
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to save " + from, e);
        }
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