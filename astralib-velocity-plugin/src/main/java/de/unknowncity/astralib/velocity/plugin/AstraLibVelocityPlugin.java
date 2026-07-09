package de.unknowncity.astralib.velocity.plugin;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import de.unknowncity.astralib.common.command.ReloadCommand;
import de.unknowncity.astralib.common.database.StandardDataBaseProvider;
import de.unknowncity.astralib.common.database.dao.language.SqlLanguageDao;
import de.unknowncity.astralib.common.database.dao.player.SqlPlayerNameDao;
import de.unknowncity.astralib.common.io.ResourceUtils;
import de.unknowncity.astralib.common.message.lang.Localization;
import de.unknowncity.astralib.common.platform.AstraLib;
import de.unknowncity.astralib.common.plugin.ReloadManager;
import de.unknowncity.astralib.common.redis.RedisService;
import de.unknowncity.astralib.common.redis.RedisUriBuilder;
import de.unknowncity.astralib.common.service.PlayerNameService;
import de.unknowncity.astralib.velocity.api.lib.AstraLibVelocity;
import de.unknowncity.astralib.velocity.api.lib.AstraLibVelocityProvider;
import de.unknowncity.astralib.velocity.api.message.VelocityMessenger;
import de.unknowncity.astralib.velocity.platform.VelocityAstraPlatform;
import de.unknowncity.astralib.velocity.plugin.configuration.AstraLibConfiguration;
import de.unknowncity.astralib.velocity.plugin.database.service.LanguageService;
import de.unknowncity.astralib.velocity.plugin.listener.PlayerDisconnectListener;
import de.unknowncity.astralib.velocity.plugin.listener.PlayerJoinListener;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.velocity.VelocityCommandManager;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = "astralib",
        name = "AstraLib",
        version = BuildConstants.VERSION,
        description = "A super cool plugin utility library",
        authors = {"UnknownCity"}
)
public class AstraLibVelocityPlugin {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private final PluginContainer pluginContainer;

    private AstraLibConfiguration configuration;
    private LanguageService languageService;
    private PlayerNameService playerNameService;
    private VelocityMessenger messenger;
    private RedisService redisService;
    private Localization localization;

    @Inject
    public AstraLibVelocityPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory, PluginContainer pluginContainer) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.pluginContainer = pluginContainer;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        initializeConfiguration();
        initializeDataServices();

        this.messenger = initializeMessenger();

        server.getEventManager().register(this, new PlayerJoinListener(this));
        server.getEventManager().register(this, new PlayerDisconnectListener(this));
        AstraLibVelocityProvider.register(new AstraLibVelocity(languageService, messenger, playerNameService));

        var platform = new VelocityAstraPlatform(this, server);

        try {
            this.redisService = new RedisService(RedisUriBuilder.build(configuration.redis()));
            AstraLib.initialize(platform, redisService);
        } catch (Exception e) {
            logger.warn("Failed to connect to Redis, continuing without Redis support: {}", e.getMessage());
            AstraLib.initialize(platform);
        }

        initializeCommands();
        ReloadManager.register("astralib", this::reloadSelf);
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        ReloadManager.unregister("astralib");
        if (AstraLib.isRedisAvailable()) {
            AstraLib.getRedis().shutdown();
        }
    }

    private void initializeConfiguration() {
        var configOpt = AstraLibConfiguration.loadFromFile(AstraLibConfiguration.class, dataDirectory);

        this.configuration = configOpt.orElseGet(AstraLibConfiguration::new);
        this.configuration.pluginDataPath(dataDirectory);
        this.configuration.save();
    }

    private void initializeCommands() {
        try {
            var commandManager = new VelocityCommandManager<>(
                    pluginContainer, server, ExecutionCoordinator.<CommandSource>simpleCoordinator(), SenderMapper.identity()
            );
            ReloadCommand.register(commandManager, messenger);
        } catch (Exception e) {
            logger.error("Failed to initialize AstraLib commands", e);
        }
    }

    private void reloadSelf() {
        initializeConfiguration();
        localization.reload();
        logger.info("Reloaded configuration and language files");
    }

    private void initializeDataServices() {
        var databaseSettings = configuration.database();

        var queryConfig = StandardDataBaseProvider.updateAndConnectToDataBase(databaseSettings, getClass().getClassLoader(), dataDirectory);

        this.languageService = new LanguageService(new SqlLanguageDao(queryConfig), configuration);
        this.playerNameService = new PlayerNameService(new SqlPlayerNameDao(queryConfig));
    }

    private VelocityMessenger initializeMessenger() {
        var resourceLogger = java.util.logging.Logger.getLogger("AstraLib");
        ResourceUtils.saveDefaultResource("lang/de_DE.yml", Path.of("lang/de_DE.yml"), dataDirectory, getClass().getClassLoader(), resourceLogger);
        ResourceUtils.saveDefaultResource("lang/en_US.yml", Path.of("lang/en_US.yml"), dataDirectory, getClass().getClassLoader(), resourceLogger);

        this.localization = Localization.builder(dataDirectory.resolve("lang"))
                .withLogger(resourceLogger)
                .buildAndLoad();
        return VelocityMessenger.builder(localization, pluginContainer.getDescription(), server)
                .withDefaultLanguage(configuration.language().defaultLanguage())
                .withLanguageService(languageService)
                .build();
    }

    public LanguageService languageService() {
        return languageService;
    }

    public PlayerNameService playerNameService() {
        return playerNameService;
    }

    public Logger logger() {
        return logger;
    }

    public ProxyServer server() {
        return server;
    }
}
