package de.unknowncity.astralib.paper.api.plugin;

import de.unknowncity.astralib.common.command.CommandRegistry;
import de.unknowncity.astralib.common.hook.HookRegistry;
import de.unknowncity.astralib.common.io.ResourceUtils;
import de.unknowncity.astralib.common.plugin.AstraPlugin;
import de.unknowncity.astralib.common.service.AstraLanguageService;
import de.unknowncity.astralib.common.service.ServiceRegistry;
import de.unknowncity.astralib.paper.api.hook.PaperPluginHook;
import de.unknowncity.astralib.paper.api.hook.defaulthooks.PlaceholderApiHook;
import de.unknowncity.astralib.paper.api.lib.AstraLibPaper;
import de.unknowncity.astralib.paper.api.message.PaperMessenger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
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

    /**
     * Registers one or multiple listeners
     * Shortcut for registering listeners though calling the PluginManager
     * @param listeners one or multiple listeners to register
     */
    public void regsterListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    // Shortcuts for scheduler tasks
    public BukkitTask runTask(Runnable runnable) {
        return getServer().getScheduler().runTask(this, runnable);
    }

    public BukkitTask runTaskLater(Runnable runnable, long delay) {
        return getServer().getScheduler().runTaskLater(this, runnable, delay);
    }

    public BukkitTask runTaskAsynchronously(Runnable runnable) {
        return getServer().getScheduler().runTaskAsynchronously(this, runnable);
    }

    public BukkitTask runTaskTimer(Runnable runnable, long delay, long period) {
        return getServer().getScheduler().runTaskTimer(this, runnable, delay, period);
    }

    public BukkitTask runTaskTimerAsynchronously(Runnable runnable, long delay, long period) {
        return getServer().getScheduler().runTaskTimerAsynchronously(this, runnable, delay, period);
    }

    // Method to initialize command manager using captions of the lib messenger

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

    @ApiStatus.Internal
    private void registerDefaultHooks() {
        hookRegistry.register(new PlaceholderApiHook(this));
    }

    /**
     * Get the plugins data path
     * @return the plugins data path
     */
    @Override
    public @NotNull Path getDataPath() {
        return super.getDataPath();
    }

    @Override
    public void onDisable() {
        onPluginDisable();
    }

    /**
     * Disables this plugin
     * Example use case: Configuration failed to load
     */
    @Override
    public void disableSelf() {
        this.getServer().getPluginManager().disablePlugin(this);
    }

    /**
     * Saves a resource from the plugin jar to a location in the plugins data folder
     * @param from the resources location in the jar
     * @param to the location the resource should be saved to, starting from the plugins data folder
     */
    @Override
    public void saveDefaultResource(String from, Path to) {
        ResourceUtils.saveDefaultResource(from, to, getDataPath(), getClassLoader(), getLogger());
    }

    /**
     * Called after the internal hooks into AstraLib have been called
     * Use this instead of onEnable when creating a plugin using AstraLib
     */
    @Override
    public void onPluginEnable() {

    }

    /**
     * Called after the internal hooks into AstraLib have been shut down
     * Use this instead of onDisable when creating a plugin using AstraLib
     */
    @Override
    public void onPluginDisable() {

    }

    /**
     * Get the plugins cloud command manager
     * @return the plugins command manager
     */
    public CommandManager<CommandSender> commandManager() {
        return commandManager;
    }
}