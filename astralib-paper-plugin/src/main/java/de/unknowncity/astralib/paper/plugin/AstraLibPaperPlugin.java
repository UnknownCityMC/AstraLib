package de.unknowncity.astralib.paper.plugin;

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
import de.unknowncity.astralib.paper.api.lib.AstraLibPaper;
import de.unknowncity.astralib.paper.api.message.PaperMessenger;
import de.unknowncity.astralib.paper.platform.PaperAstraPlatform;
import de.unknowncity.astralib.paper.plugin.configuration.AstraLibConfiguration;
import de.unknowncity.astralib.paper.plugin.database.service.LanguageService;
import de.unknowncity.astralib.paper.plugin.listener.PlayerJoinListener;
import de.unknowncity.astralib.paper.plugin.listener.PlayerQuitListener;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import java.nio.file.Path;
import java.util.logging.Level;

public class AstraLibPaperPlugin extends JavaPlugin {
    private PaperMessenger messenger;
    private AstraLibConfiguration configuration;
    private LanguageService languageService;
    private PlayerNameService playerNameService;
    private Localization localization;

    @Override
    public void onEnable() {
        initializeConfiguration();
        initializeDataServices();

        this.messenger = initializeMessenger();

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getServicesManager().register(AstraLibPaper.class, new AstraLibPaper(
            languageService,
            messenger,
            playerNameService
        ), this, ServicePriority.High);
        var platform = new PaperAstraPlatform(this);

        try {
            var redisService = new RedisService(RedisUriBuilder.build(configuration.redis()));
            AstraLib.initialize(platform, redisService);
        } catch (Exception e) {
            getLogger().warning("Failed to connect to Redis, continuing without Redis support: " + e.getMessage());
            AstraLib.initialize(platform);
        }

        initializeCommands();
        ReloadManager.register("AstraLib", this::reloadSelf);

        // Players that joined before this feature existed are known to the server,
        // make them resolvable through the name service too
        getServer().getScheduler().runTaskAsynchronously(this, this::backfillPlayerNameCache);
    }

    private void backfillPlayerNameCache() {
        for (var offlinePlayer : getServer().getOfflinePlayers()) {
            var name = offlinePlayer.getName();
            if (name != null) {
                playerNameService.cachePlayer(offlinePlayer.getUniqueId(), name);
            }
        }
    }

    @Override
    public void onDisable() {
        ReloadManager.unregister("AstraLib");
        if (AstraLib.isRedisAvailable()) {
            AstraLib.getRedis().shutdown();
        }
    }

    private void initializeConfiguration() {
        var configOpt = AstraLibConfiguration.loadFromFile(AstraLibConfiguration.class);

        this.configuration = configOpt.orElseGet(AstraLibConfiguration::new);
        this.configuration.save();
    }

    private void initializeCommands() {
        try {
            var commandManager = LegacyPaperCommandManager.createNative(
                    this, ExecutionCoordinator.<CommandSender>simpleCoordinator()
            );
            ReloadCommand.register(commandManager, messenger);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to initialize AstraLib commands", e);
        }
    }

    private void reloadSelf() {
        initializeConfiguration();
        localization.reload();
        getLogger().info("Reloaded configuration and language files");
    }

    public PaperMessenger initializeMessenger() {
        ResourceUtils.saveDefaultResource("lang/de_DE.yml", Path.of("lang/de_DE.yml"), getDataPath(), getClassLoader(), getLogger());
        ResourceUtils.saveDefaultResource("lang/en_US.yml", Path.of("lang/en_US.yml"), getDataPath(), getClassLoader(), getLogger());

        this.localization = Localization.builder(getDataPath().resolve("lang"))
                .withLogger(getLogger())
                .buildAndLoad();
        return PaperMessenger.builder(localization, getPluginMeta())
                .withDefaultLanguage(configuration.language().defaultLanguage())
                .withLanguageService(languageService)
                .build();
    }

    private void initializeDataServices() {

        var databaseSettings = configuration.database();

        var queryConfig = StandardDataBaseProvider.updateAndConnectToDataBase(databaseSettings, getClassLoader(), getDataPath());

        this.languageService = new LanguageService(new SqlLanguageDao(queryConfig), configuration);
        this.playerNameService = new PlayerNameService(new SqlPlayerNameDao(queryConfig));
    }

    public LanguageService languageService() {
        return languageService;
    }

    public PlayerNameService playerNameService() {
        return playerNameService;
    }
}
