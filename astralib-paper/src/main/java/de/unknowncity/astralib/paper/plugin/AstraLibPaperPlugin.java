package de.unknowncity.astralib.paper.plugin;

import de.unknowncity.astralib.common.configuration.setting.defaults.DataBaseSetting;
import de.unknowncity.astralib.common.configuration.setting.serializer.DatabaseSettingSerializer;
import de.unknowncity.astralib.common.database.DataBaseProvider;
import de.unknowncity.astralib.common.database.DataBaseUpdater;
import de.unknowncity.astralib.common.message.lang.Localization;
import de.unknowncity.astralib.paper.api.hook.defaulthooks.PlaceholderApiHook;
import de.unknowncity.astralib.paper.api.message.PaperMessenger;
import de.unknowncity.astralib.paper.api.plugin.PaperAstraPlugin;
import de.unknowncity.astralib.paper.plugin.command.LanguageCommand;
import de.unknowncity.astralib.paper.plugin.configuration.AstraLibConfig;
import de.unknowncity.astralib.paper.plugin.configuration.serializer.AstraLibConfigSerializer;
import de.unknowncity.astralib.paper.plugin.configuration.serializer.LanguageSettingSerializer;
import de.unknowncity.astralib.paper.plugin.configuration.settings.LanguageSetting;
import de.unknowncity.astralib.paper.plugin.database.DataBaseButler;
import de.unknowncity.astralib.paper.plugin.database.service.LanguageService;
import de.unknowncity.astralib.paper.plugin.listener.PlayerJoinListener;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.logging.Level;

public class AstraLibPaperPlugin extends PaperAstraPlugin {
    private PaperMessenger messenger;
    private AstraLibConfig configuration;
    private LanguageService languageService;
    private Localization localization;

    @Override
    public void onPluginEnable() {
        this.configuration = new AstraLibConfig(
                new DataBaseSetting(),
                new LanguageSetting()
        );

        this.configLoader.saveDefaultConfig(
                configuration
                , getDataPath().getParent().resolve(ASTRA_LIB_MAIN_CONFIG_PATH), builder -> {
                    builder.register(AstraLibConfig.class, new AstraLibConfigSerializer());
                    builder.register(DataBaseSetting.class, new DatabaseSettingSerializer());
                    builder.register(LanguageSetting.class, new LanguageSettingSerializer());
                }
        );

        var configOpt = this.configLoader.loadConfiguration(getDataPath().getParent().resolve(ASTRA_LIB_MAIN_CONFIG_PATH), AstraLibConfig.class, builder -> {
            builder.register(AstraLibConfig.class, new AstraLibConfigSerializer());
            builder.register(DataBaseSetting.class, new DatabaseSettingSerializer());
            builder.register(LanguageSetting.class, new LanguageSettingSerializer());
        });

        registerHooks();

        if (configOpt.isPresent()) {
            this.configuration = configOpt.get();
        } else {
            disableSelf();
        }

        this.configLoader.saveDefaultConfigFile(Path.of("en_US.yml"), getDataPath().resolve("lang").resolve("en_US.yml"));
        this.configLoader.saveDefaultConfigFile(Path.of("de_DE.yml"), getDataPath().resolve("lang").resolve("de_DE.yml"));

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

        var dataBaseProvider = new DataBaseProvider(databaseSettings);
        var dataSource = dataBaseProvider.createDataSource();
        dataBaseProvider.setup(dataSource, getLogger());

        var dataBaseUpdater = new DataBaseUpdater(dataSource, databaseSettings);
        try {
            dataBaseUpdater.update(getClassLoader());
        } catch (IOException | SQLException e) {
            this.getLogger().log(Level.SEVERE, "Failed to update database", e);
            this.getServer().getPluginManager().disablePlugin(this);
        }

        var databaseButler = new DataBaseButler(databaseSettings);

        this.languageService = new LanguageService(databaseButler.serveLanguageDao(), configuration);
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
