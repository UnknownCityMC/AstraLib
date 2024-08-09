package de.unknowncity.astralib.common.database;

import com.zaxxer.hikari.HikariDataSource;
import de.chojo.sadu.datasource.DataSourceCreator;
import de.chojo.sadu.mariadb.databases.MariaDb;
import de.chojo.sadu.mysql.databases.MySql;
import de.chojo.sadu.postgresql.databases.PostgreSql;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.chojo.sadu.sqlite.databases.SqLite;
import de.unknowncity.astralib.common.configuration.AstraConfiguration;
import de.unknowncity.astralib.common.configuration.setting.defaults.DataBaseSetting;

import java.sql.Driver;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataBaseProvider {
    private final DataBaseSetting dataBaseSettings;

    public DataBaseProvider(DataBaseSetting dataBaseSettings) {
        this.dataBaseSettings = dataBaseSettings;
    }

    public void setup(HikariDataSource dataSource, Logger logger) {
        var config = QueryConfiguration.builder(dataSource)
                .setExceptionHandler(err -> logger.log(Level.SEVERE, "An error occured during a database request", err))
                .build();

        QueryConfiguration.setDefault(config);
    }

    public HikariDataSource createDataSource() {
        switch (dataBaseSettings.dataBaseDriver()) {
            case MARIADB -> {
                return DataSourceCreator.create(MariaDb.get())
                        .configure(config -> config
                                .port(dataBaseSettings.port())
                                .user(dataBaseSettings.userName())
                                .password(dataBaseSettings.password())
                                .database(dataBaseSettings.database())
                                .host(dataBaseSettings.host())
                                .driverClass(Driver.class)
                        )
                        .create()
                        .withMaximumPoolSize(dataBaseSettings.maxPoolSize())
                        .withMinimumIdle(dataBaseSettings.minIdleConnections())
                        .build();
            }
            case MYSQL -> {
                return DataSourceCreator.create(MySql.get())
                        .configure(config -> config
                                .port(dataBaseSettings.port())
                                .user(dataBaseSettings.userName())
                                .password(dataBaseSettings.password())
                                .database(dataBaseSettings.database())
                                .host(dataBaseSettings.host())
                                .driverClass(Driver.class)
                        )
                        .create()
                        .withMaximumPoolSize(dataBaseSettings.maxPoolSize())
                        .withMinimumIdle(dataBaseSettings.minIdleConnections())
                        .build();
            }
            case POSTGRESQL -> {
                return DataSourceCreator.create(PostgreSql.get())
                        .configure(config -> config
                                .port(dataBaseSettings.port())
                                .user(dataBaseSettings.userName())
                                .password(dataBaseSettings.password())
                                .database(dataBaseSettings.database())
                                .host(dataBaseSettings.host())
                                .driverClass(Driver.class)
                        )
                        .create()
                        .withMaximumPoolSize(dataBaseSettings.maxPoolSize())
                        .withMinimumIdle(dataBaseSettings.minIdleConnections())
                        .build();
            }
            default -> {
                return DataSourceCreator.create(SqLite.get())
                        .configure(config -> config
                                .path(dataBaseSettings.sqliteDataBasePath())
                                .driverClass(Driver.class)
                        )
                        .create()
                        .withMaximumPoolSize(dataBaseSettings.maxPoolSize())
                        .withMinimumIdle(dataBaseSettings.minIdleConnections())
                        .build();
            }
        }
    }
}
