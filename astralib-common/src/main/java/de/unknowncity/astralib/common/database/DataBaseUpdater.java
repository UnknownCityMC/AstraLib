package de.unknowncity.astralib.common.database;

import de.chojo.sadu.mariadb.databases.MariaDb;
import de.chojo.sadu.mysql.databases.MySql;
import de.chojo.sadu.postgresql.databases.PostgreSql;
import de.chojo.sadu.sqlite.databases.SqLite;
import de.chojo.sadu.updater.SqlUpdater;
import de.unknowncity.astralib.common.configuration.setting.defaults.DataBaseSetting;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

public class DataBaseUpdater {
    private final DataSource dataSource;
    private final DataBaseSetting dataBaseSettings;

    public DataBaseUpdater(DataSource dataSource, DataBaseSetting dataBaseSettings) {
        this.dataSource = dataSource;
        this.dataBaseSettings = dataBaseSettings;
    }

    public void update(ClassLoader classLoader) throws IOException, SQLException {
        switch (dataBaseSettings.dataBaseDriver()) {
            case MARIADB -> SqlUpdater.builder(dataSource, MariaDb.get())
                    .setVersionTable("version")
                    .withClassLoader(classLoader)
                    .execute();

            case MYSQL -> SqlUpdater.builder(dataSource, MySql.get())
                    .setVersionTable("version")
                    .withClassLoader(classLoader)
                    .execute();

            case POSTGRESQL -> SqlUpdater.builder(dataSource, PostgreSql.get())
                    .setVersionTable("version")
                    .withClassLoader(classLoader)
                    .execute();

            default -> SqlUpdater.builder(dataSource, SqLite.get())
                    .setVersionTable("version")
                    .withClassLoader(classLoader)
                    .execute();
        }
    }
}
