package de.unknowncity.astralib.paper.plugin;

import de.unknowncity.astralib.common.configuration.YamlAstraConfiguration;
import de.unknowncity.astralib.common.database.StandardDataBaseProvider;
import de.unknowncity.astralib.common.message.lang.Localization;
import de.unknowncity.astralib.paper.api.hook.defaulthooks.PlaceholderApiHook;
import de.unknowncity.astralib.paper.api.message.PaperMessenger;
import de.unknowncity.astralib.paper.api.plugin.PaperAstraPlugin;
import de.unknowncity.astralib.paper.plugin.command.LanguageCommand;
import de.unknowncity.astralib.paper.plugin.configuration.AstraLibConfiguration;
import de.unknowncity.astralib.paper.plugin.database.dao.language.MariaDBLanguageDao;
import de.unknowncity.astralib.paper.plugin.database.service.LanguageService;
import de.unknowncity.astralib.paper.plugin.listener.PlayerJoinListener;

import java.nio.file.Path;

public class AstraLibPaperPlugin extends PaperAstraPlugin {
    private PaperMessenger messenger;
    private AstraLibConfiguration configuration;
    private LanguageService languageService;
    private Localization localization;

    @Override
    public void onPluginEnable() {
        var configOpt = YamlAstraConfiguration.loadFromFile(AstraLibConfiguration.class);

        this.configuration = configOpt.orElseGet(AstraLibConfiguration::new);
        this.configuration.save();

        registerHooks();

        this.saveDefaultResource(Path.of("en_US.yml"), Path.of("lang/en_US.yml"));
        this.saveDefaultResource(Path.of("de_DE.yml"), Path.of("lang/de_DE.yml"));

        initializeDataServices();

        initializeMessenger();

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        new LanguageCommand(this).apply(commandManager);
    }

    private void registerHooks() {
        this.hookRegistry.register(new PlaceholderApiHook(getServer()));
    }

    private void initializeDataServices() {

        var databaseSettings = configuration.dataBaseSetting();

        StandardDataBaseProvider.updateAndConnectToDataBase(databaseSettings, getClassLoader());

        this.languageService = new LanguageService(new MariaDBLanguageDao(), configuration);
    }

    private void initializeMessenger() {
        var defaultLang = configuration.languageSetting().defaultLanguage();

        this.localization = Localization.builder(getDataPath().resolve("lang")).withLogger(getLogger()).buildAndLoad();
        var papiHook = hookRegistry.getRegistered(PlaceholderApiHook.class);

        this.messenger = PaperMessenger.builder(localization)
                .withPlaceHolderAPI(papiHook)
                .withDefaultLanguage(defaultLang)
                .withLanguageService(languageService)
                .build();
    }

    public Localization localization() {
        return localization;
    }

    public LanguageService languageService() {
        return languageService;
    }

    public PaperMessenger messenger() {
        return messenger;
    }
}
