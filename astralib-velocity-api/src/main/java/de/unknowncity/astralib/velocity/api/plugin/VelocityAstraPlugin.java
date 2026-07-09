package de.unknowncity.astralib.velocity.api.plugin;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import de.unknowncity.astralib.common.command.CommandRegistry;
import de.unknowncity.astralib.common.command.SimpleCloudCaptionRegistry;
import de.unknowncity.astralib.common.hook.HookRegistry;
import de.unknowncity.astralib.common.io.ResourceUtils;
import de.unknowncity.astralib.common.plugin.AstraPlugin;
import de.unknowncity.astralib.common.plugin.ReloadManager;
import de.unknowncity.astralib.common.service.AstraLanguageService;
import de.unknowncity.astralib.common.service.PlayerNameService;
import de.unknowncity.astralib.common.service.ServiceRegistry;
import de.unknowncity.astralib.velocity.api.hook.VelocityPluginHook;
import de.unknowncity.astralib.velocity.api.lib.AstraLibVelocityProvider;
import de.unknowncity.astralib.velocity.api.message.VelocityMessenger;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.caption.CaptionRegistry;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.velocity.VelocityCommandManager;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.time.Duration;

/**
 * Base class for velocity plugins built on AstraLib.
 * <p>
 * Subclasses MUST declare a dependency on AstraLib in their plugin annotation,
 * otherwise velocity does not guarantee that AstraLib initializes first:
 * <pre>{@code @Plugin(id = "myplugin", dependencies = {@Dependency(id = "astralib")})}</pre>
 */
public abstract class VelocityAstraPlugin implements AstraPlugin {
    protected final ProxyServer server;
    protected final Logger logger;
    protected final Path dataPath;
    protected final PluginContainer pluginContainer;

    protected VelocityCommandManager<CommandSource> commandManager;
    protected CommandRegistry<CommandSource, VelocityAstraPlugin> commandRegistry;

    protected ServiceRegistry<VelocityAstraPlugin> serviceRegistry;
    protected CaptionRegistry<CommandSource> captionRegistry;
    protected HookRegistry<VelocityAstraPlugin, VelocityPluginHook> hookRegistry;

    protected AstraLanguageService<Player> languageService;
    protected PlayerNameService playerNameService;
    protected VelocityMessenger libMessenger;

    private final java.util.logging.Logger resourceLogger;

    public VelocityAstraPlugin(ProxyServer server, Logger logger, Path dataPath, PluginContainer pluginContainer) {
        this.server = server;
        this.logger = logger;
        this.dataPath = dataPath;
        this.pluginContainer = pluginContainer;
        this.resourceLogger = java.util.logging.Logger.getLogger(pluginContainer.getDescription().getId());
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        this.hookRegistry = new HookRegistry<>(this);
        this.serviceRegistry = new ServiceRegistry<>(this);

        if (!AstraLibVelocityProvider.isAvailable()) {
            logger.error("AstraLib velocity is not present!");
            disableSelf();
            return;
        }

        var astraLib = AstraLibVelocityProvider.get();

        this.languageService = astraLib.astraLanguageService();
        this.playerNameService = astraLib.playerNameService();
        this.libMessenger = astraLib.velocityMessenger();

        initializeCommandManager(libMessenger);

        // Plugin exclusive logic starts here
        onPluginEnable();

        ReloadManager.register(pluginContainer.getDescription().getId(), this::onPluginReload);
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        ReloadManager.unregister(pluginContainer.getDescription().getId());
        onPluginDisable();
    }

    /**
     * Registers one or multiple listeners
     * Shortcut for registering listeners though calling the EventManager
     * @param listeners one or multiple listeners to register
     */
    public void registerListeners(Object... listeners) {
        for (Object listener : listeners) {
            server.getEventManager().register(this, listener);
        }
    }

    // Shortcuts for scheduler tasks (the proxy has no main thread, everything runs async)
    public ScheduledTask runTask(Runnable runnable) {
        return server.getScheduler().buildTask(this, runnable).schedule();
    }

    public ScheduledTask runTaskLater(Runnable runnable, Duration delay) {
        return server.getScheduler().buildTask(this, runnable).delay(delay).schedule();
    }

    public ScheduledTask runTaskTimer(Runnable runnable, Duration delay, Duration period) {
        return server.getScheduler().buildTask(this, runnable).delay(delay).repeat(period).schedule();
    }

    // Method to initialize command manager using captions of the lib messenger


    public void initializeCommandManager(VelocityMessenger messenger) {
        try {
            this.commandManager = new VelocityCommandManager<>(
                    pluginContainer, server, ExecutionCoordinator.simpleCoordinator(), SenderMapper.identity()
            );
        } catch (Exception e) {
            logger.error("Failed to initialize command manager", e);
            disableSelf();
            return;
        }

        this.captionRegistry = commandManager.captionRegistry();
        this.commandRegistry = new CommandRegistry<>(this, commandManager);

        SimpleCloudCaptionRegistry.registerDefaultCaptions(commandManager, messenger,
                sender -> sender instanceof Player player ? player : null);
    }

    /**
     * Get the plugins data path
     * @return the plugins data path
     */
    public Path getDataPath() {
        return dataPath;
    }

    /**
     * Velocity cannot disable plugins at runtime, so this unregisters
     * all listeners of this plugin instead
     */
    @Override
    public void disableSelf() {
        server.getEventManager().unregisterListeners(this);
    }

    /**
     * Saves a resource from the plugin jar to a location in the plugins data folder
     * @param from the resources location in the jar
     * @param to the location the resource should be saved to, starting from the plugins data folder
     */
    @Override
    public void saveDefaultResource(String from, Path to) {
        ResourceUtils.saveDefaultResource(from, to, dataPath, getClass().getClassLoader(), resourceLogger);
    }

    /**
     * Called after the internal hooks into AstraLib have been called
     * Use this instead of listening to ProxyInitializeEvent when creating a plugin using AstraLib
     */
    @Override
    public void onPluginEnable() {

    }

    /**
     * Called on proxy shutdown
     * Use this instead of listening to ProxyShutdownEvent when creating a plugin using AstraLib
     */
    @Override
    public void onPluginDisable() {

    }

    /**
     * Called when the plugin gets reloaded via /astralib reload [plugin]
     * Override to reload configurations, language files etc.
     */
    @Override
    public void onPluginReload() {

    }

    /**
     * Get the plugins cloud command manager
     * @return the plugins command manager
     */
    public CommandManager<CommandSource> commandManager() {
        return commandManager;
    }

    /**
     * Get the plugins command registry
     * @return the plugins command registry
     */
    public CommandRegistry<CommandSource, VelocityAstraPlugin> commandRegistry() {
        return commandRegistry;
    }

    /**
     * Get the proxy server this plugin is running on
     * @return the proxy server
     */
    public ProxyServer server() {
        return server;
    }
}
