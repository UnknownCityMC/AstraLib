package de.unknowncity.astralib.paper.plugin.database;

import de.unknowncity.astralib.common.configuration.setting.defaults.DataBaseSetting;
import de.unknowncity.astralib.paper.plugin.database.dao.language.LanguageDao;
import de.unknowncity.astralib.paper.plugin.database.dao.language.MySQLLanguageDao;

import javax.sql.DataSource;

public class DataBaseButler {

    private final DataBaseSetting dataBaseSettings;

    public DataBaseButler(DataBaseSetting dataBaseSettings) {
        this.dataBaseSettings = dataBaseSettings;
    }

    //TODO do implementations for other databases
    public LanguageDao serveLanguageDao() {
        return switch (dataBaseSettings.dataBaseDriver()) {
            default -> new MySQLLanguageDao();
        };
    }
}
