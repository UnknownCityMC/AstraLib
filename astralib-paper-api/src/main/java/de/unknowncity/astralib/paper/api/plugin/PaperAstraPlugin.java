package de.unknowncity.astralib.paper.api.plugin;

import de.unknowncity.astralib.common.command.CommandRegistry;
import de.unknowncity.astralib.common.hook.HookRegistry;
import de.unknowncity.astralib.common.plugin.AstraPlugin;
import de.unknowncity.astralib.common.service.AstraLanguageService;
import de.unknowncity.astralib.common.service.ServiceRegistry;
import de.unknowncity.astralib.paper.api.hook.PaperPluginHook;
import de.unknowncity.astralib.paper.api.hook.defaulthooks.PlaceholderApiHook;
import de.unknowncity.astralib.paper.api.lib.AstraLibPaper;
import de.unknowncity.astralib.paper.api.message.PaperMessenger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.caption.CaptionProvider;
import org.incendo.cloud.caption.CaptionRegistry;
import org.incendo.cloud.caption.StandardCaptionKeys;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.NodePath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

public class PaperAstraPlugin extends JavaPlugin implements AstraPlugin {
    protected LegacyPaperCommandManager<CommandSender> commandManager;
    protected CommandRegistry<CommandSender, PaperAstraPlugin> commandRegistry;

    protected ServiceRegistry<PaperAstraPlugin> serviceRegistry;
    protected CaptionRegistry<CommandSender> captionRegistry;
    protected HookRegistry<PaperAstraPlugin, PaperPluginHook> hookRegistry;

    protected AstraLanguageService<Player> languageService;
    protected PaperMessenger libMessenger;

    @Override
    public void onEnable() {
        this.hookRegistry = new HookRegistry<>(this);
        registerDefaultHooks();

        this.serviceRegistry = new ServiceRegistry<>(this);

        var astraLib = getServer().getServicesManager().getRegistration(AstraLibPaper.class);

        if (astraLib == null) {
            disableSelf();
            getLogger().log(Level.SEVERE, "AstraLib paper is not present!");
            return;
        }

        this.languageService = astraLib.getProvider().astraLanguageService();
        this.libMessenger = astraLib.getProvider().paperMessenger();

        initializeCommandManager(libMessenger);

        // Plugin exclusive logic starts here
        onPluginEnable();
    }

    @ApiStatus.Internal
    public void initializeCommandManager(PaperMessenger messenger) {
        try {
            this.commandManager = LegacyPaperCommandManager.createNative(
                    this, ExecutionCoordinator.simpleCoordinator()
            );

        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Failed to initialize command manager", e);
            disableSelf();
            return;
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
        hookRegistry.register(new PlaceholderApiHook(this));
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
    public void saveDefaultResource(String from, Path to) {
        if (Files.exists(getDataPath().resolve(to))) {
            return;
        }

        try (var resourceAsStream = getClassLoader().getResourceAsStream(from)) {
            if (resourceAsStream == null) {
                getLogger().log(
                        Level.SEVERE, "Failed to save " + from + ". " +
                                "The plugin developer tried to save a file that does not exist in the plugins jar file!"
                );
                return;
            }
            Files.createDirectories(getDataPath().resolve(to.getParent()));
            try (var outputStream = Files.newOutputStream(getDataPath().resolve(to))) {
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

    public CommandManager<CommandSender> commandManager() {
        return commandManager;
    }
}