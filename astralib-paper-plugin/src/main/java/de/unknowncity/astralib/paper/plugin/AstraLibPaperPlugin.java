package de.unknowncity.astralib.paper.plugin;

import de.unknowncity.astralib.common.configuration.YamlAstraConfiguration;
import de.unknowncity.astralib.common.database.StandardDataBaseProvider;
import de.unknowncity.astralib.common.message.lang.Localization;
import de.unknowncity.astralib.paper.api.lib.AstraLibPaper;
import de.unknowncity.astralib.paper.api.message.PaperMessenger;
import de.unknowncity.astralib.paper.plugin.configuration.AstraLibConfiguration;
import de.unknowncity.astralib.paper.plugin.database.dao.language.MariaDBLanguageDao;
import de.unknowncity.astralib.paper.plugin.database.service.LanguageService;
import de.unknowncity.astralib.paper.plugin.listener.PlayerJoinListener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class AstraLibPaperPlugin extends JavaPlugin {
    private PaperMessenger messenger;
    private AstraLibConfiguration configuration;
    private LanguageService languageService;

    @Override
    public void onEnable() {
        var configOpt = AstraLibConfiguration.loadFromFile(AstraLibConfiguration.class);

        this.configuration = configOpt.orElseGet(AstraLibConfiguration::new);
        this.configuration.save();

        initializeDataServices();

        this.messenger = initMesseneger();

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getServicesManager().register(AstraLibPaper.class, new AstraLibPaper(
            languageService,
            messenger
        ), this, ServicePriority.High);
    }

    public PaperMessenger initMesseneger() {
        var localization = Localization.builder(getDataPath().resolve("lang"))
                .withLogger(getLogger())
                .buildAndLoad();
        return PaperMessenger.builder(localization)
                .withDefaultLanguage(configuration.languageSetting().defaultLanguage())
                .withLanguageService(languageService)
                .build();
    }

    private void initializeDataServices() {

        var databaseSettings = configuration.dataBaseSetting();

        var queryConfig = StandardDataBaseProvider.updateAndConnectToDataBase(databaseSettings, getClassLoader(), getDataPath());

        this.languageService = new LanguageService(new MariaDBLanguageDao(queryConfig), configuration);
    }

    public LanguageService languageService() {
        return languageService;
    }
}
