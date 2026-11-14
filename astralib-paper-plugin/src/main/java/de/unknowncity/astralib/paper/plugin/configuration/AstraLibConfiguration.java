package de.unknowncity.astralib.paper.plugin.configuration;

import de.unknowncity.astralib.common.config.DatabaseSettings;
import de.unknowncity.astralib.common.config.RedisSettings;
import de.unknowncity.astralib.common.database.DatabaseDriver;
import de.unknowncity.astralib.paper.plugin.configuration.settings.LanguageSetting;

public class AstraLibConfiguration {
    private DatabaseSettings database = new DatabaseSettings(DatabaseDriver.SQLITE, "sqlite.db", 3306, "astralib", "", "");

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
