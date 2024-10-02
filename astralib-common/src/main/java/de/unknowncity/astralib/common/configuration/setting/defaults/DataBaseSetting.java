package de.unknowncity.astralib.common.configuration.setting.defaults;

import de.unknowncity.astralib.common.configuration.setting.Setting;
import de.unknowncity.astralib.common.database.DataBaseDriver;

import java.nio.file.Path;

/**
 * @deprecated version 0.4.0 introduces a new config system based on jackson
 * Use {@link ModernDataBaseSetting} instead
 */
@Deprecated(forRemoval = true, since = "0.4.0")
public class DataBaseSetting implements Setting {
    private DataBaseDriver dataBaseDriver = DataBaseDriver.MARIADB;
    private String host = "localhost";
    private int port = 3306;
    private String database = "astra_lib";
    private String userName = "astra_user";
    private String password = "goblin_secret";
    private int maxPoolSize = 3;
    private int minIdleConnections = 1;
    private Path sqliteDataBasePath = Path.of("/");

    public DataBaseSetting(
            DataBaseDriver dataBaseDriver,
            String host,
            int port,
            String database,
            String userName,
            String password,
            int maxPoolSize,
            int minIdleConnections,
            Path sqliteDataBasePath
    ) {
        this.dataBaseDriver = dataBaseDriver;
        this.host = host;
        this.port = port;
        this.database = database;
        this.userName = userName;
        this.password = password;
        this.maxPoolSize = maxPoolSize;
        this.minIdleConnections = minIdleConnections;
        this.sqliteDataBasePath = sqliteDataBasePath;
    }

    public DataBaseSetting() {

    }

    public Path sqliteDataBasePath() {
        return sqliteDataBasePath;
    }

    public DataBaseDriver dataBaseDriver() {
        return dataBaseDriver;
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public String database() {
        return database;
    }

    public String userName() {
        return userName;
    }

    public String password() {
        return password;
    }

    public int maxPoolSize() {
        return maxPoolSize;
    }

    public int minIdleConnections() {
        return minIdleConnections;
    }
}