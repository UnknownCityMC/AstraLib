package de.unknowncity.astralib.paper.plugin.configuration;

import de.unknowncity.astralib.common.config.RedisSettings;
import de.unknowncity.astralib.common.config.DatabaseSettings;
import de.unknowncity.astralib.paper.plugin.configuration.settings.LanguageSetting;

public class AstraLibConfiguration {
    private DatabaseSettings database = new DatabaseSettings();

    private LanguageSetting language = new LanguageSetting();

    private RedisSettings redis = new RedisSettings();

    public LanguageSetting language() {
        return language;
    }

    public DatabaseSettings database() {
        return database;
    }

    public RedisSettings redis() {
        return redis;
    }
}
