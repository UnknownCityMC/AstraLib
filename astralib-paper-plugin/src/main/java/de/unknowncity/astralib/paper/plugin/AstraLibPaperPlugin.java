package de.unknowncity.astralib.paper.plugin;

import de.unknowncity.astralib.common.database.StandardDataBaseProvider;
import de.unknowncity.astralib.common.io.ResourceUtils;
import de.unknowncity.astralib.common.message.lang.Localization;
import de.unknowncity.astralib.common.platform.AstraLib;
import de.unknowncity.astralib.paper.api.lib.AstraLibPaper;
import de.unknowncity.astralib.paper.api.message.PaperMessenger;
import de.unknowncity.astralib.paper.platform.PaperAstraPlatform;
import de.unknowncity.astralib.paper.plugin.configuration.AstraLibConfiguration;
import de.unknowncity.astralib.paper.plugin.database.dao.language.MariaDBLanguageDao;
import de.unknowncity.astralib.paper.plugin.database.service.LanguageService;
import de.unknowncity.astralib.paper.plugin.listener.PlayerJoinListener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

public class AstraLibPaperPlugin extends JavaPlugin {
    private PaperMessenger messenger;
    private AstraLibConfiguration configuration;
    private LanguageService languageService;

    @Override
    public void onEnable() {
        initializeConfiguration();
        initializeDataServices();

        this.messenger = initMesseneger();

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getServicesManager().register(AstraLibPaper.class, new AstraLibPaper(
            languageService,
            messenger
        ), this, ServicePriority.High);
        var platform = new PaperAstraPlatform(this);

        AstraLib.initialize(
                platform
        );
    }

    @Override
    public void onDisable() {
        AstraLib.getRedis().shutdown();
    }

    private void initializeConfiguration() {
        var configOpt = AstraLibConfiguration.loadFromFile(AstraLibConfiguration.class);

        this.configuration = configOpt.orElseGet(AstraLibConfiguration::new);
        this.configuration.save();
    }

    public PaperMessenger initMesseneger() {
        ResourceUtils.saveDefaultResource("lang/de_DE.yml", Path.of("lang/de_DE.yml"), getDataPath(), getClassLoader(), getLogger());
        ResourceUtils.saveDefaultResource("lang/en_US.yml", Path.of("lang/en_US.yml"), getDataPath(), getClassLoader(), getLogger());

        var localization = Localization.builder(getDataPath().resolve("lang"))
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

        this.languageService = new LanguageService(new MariaDBLanguageDao(queryConfig), configuration);
    }

    public LanguageService languageService() {
        return languageService;
    }
}
