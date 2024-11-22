package de.unknowncity.astralib.common.database;

import com.zaxxer.hikari.HikariDataSource;
import de.chojo.sadu.datasource.DataSourceCreator;
import de.chojo.sadu.mariadb.databases.MariaDb;
import de.chojo.sadu.mysql.databases.MySql;
import de.chojo.sadu.postgresql.databases.PostgreSql;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.chojo.sadu.sqlite.databases.SqLite;
import de.chojo.sadu.updater.SqlUpdater;
import de.unknowncity.astralib.common.configuration.setting.defaults.ModernDataBaseSetting;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StandardDataBaseProvider {

    private static QueryConfiguration setup(HikariDataSource dataSource) {
        return QueryConfiguration.builder(dataSource)
                .setExceptionHandler(err -> Logger.getLogger("DataBaseProvider").log(Level.SEVERE, "An error occurred during a database request", err))
                .build();
    }

    public static QueryConfiguration updateAndConnectToDataBase(ModernDataBaseSetting dataBaseSetting, ClassLoader classLoader, Path pluginDataPath) {
        var dataSource = createDataSource(dataBaseSetting, pluginDataPath);
        var config = setup(dataSource);
        try {
            update(dataBaseSetting.dataBaseDriver(), dataSource, classLoader);
        } catch (SQLException | IOException e) {
            Logger.getLogger("DataBaseProvider").log(Level.SEVERE, "An error occurred during a database request", e);
        }
        return config;
    }

    private static void update(DatabaseDriver dataBaseDriver, HikariDataSource dataSource, ClassLoader classLoader) throws IOException, SQLException {
        switch (dataBaseDriver) {
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

    private static HikariDataSource createDataSource(ModernDataBaseSetting dataBaseSetting, Path pluginDataPath) {
        switch (dataBaseSetting.dataBaseDriver()) {
            case MARIADB -> {
                return DataSourceCreator.create(MariaDb.get())
                        .configure(config -> config
                                .port(dataBaseSetting.port())
                                .user(dataBaseSetting.username())
                                .password(dataBaseSetting.password())
                                .database(dataBaseSetting.database())
                                .host(dataBaseSetting.host())
                                .driverClass(Driver.class)
                        )
                        .create()
                        .withMaximumPoolSize(dataBaseSetting.maxPoolSize())
                        .withMinimumIdle(dataBaseSetting.minIdleConnections())
                        .build();
            }
            case MYSQL -> {
                return DataSourceCreator.create(MySql.get())
                        .configure(config -> config
                                .port(dataBaseSetting.port())
                                .user(dataBaseSetting.username())
                                .password(dataBaseSetting.password())
                                .database(dataBaseSetting.database())
                                .host(dataBaseSetting.host())
                                .driverClass(Driver.class)
                        )
                        .create()
                        .withMaximumPoolSize(dataBaseSetting.maxPoolSize())
                        .withMinimumIdle(dataBaseSetting.minIdleConnections())
                        .build();
            }
            case POSTGRESQL -> {
                return DataSourceCreator.create(PostgreSql.get())
                        .configure(config -> config
                                .port(dataBaseSetting.port())
                                .user(dataBaseSetting.username())
                                .password(dataBaseSetting.password())
                                .database(dataBaseSetting.database())
                                .host(dataBaseSetting.host())
                                .driverClass(Driver.class)
                        )
                        .create()
                        .withMaximumPoolSize(dataBaseSetting.maxPoolSize())
                        .withMinimumIdle(dataBaseSetting.minIdleConnections())
                        .build();
            }
            default -> {
                return DataSourceCreator.create(SqLite.get())
                        .configure(config -> config
                                .path(pluginDataPath.resolve(dataBaseSetting.sqliteDataBasePath()))
                                .driverClass(Driver.class)
                        )
                        .create()
                        .withMaximumPoolSize(dataBaseSetting.maxPoolSize())
                        .withMinimumIdle(dataBaseSetting.minIdleConnections())
                        .build();
            }
        }
    }
}
