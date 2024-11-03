package de.unknowncity.astralib.common.configuration.setting.defaults;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unknowncity.astralib.common.database.DatabaseDriver;

import java.nio.file.Path;

public class ModernDataBaseSetting {
    @JsonProperty
    private DatabaseDriver dataBaseDriver = DatabaseDriver.MARIADB;
    @JsonProperty
    private String host = "localhost";
    @JsonProperty
    private int port = 3306;
    @JsonProperty
    private String database = "astra_lib";
    @JsonProperty
    private String username = "astra_user";
    @JsonProperty
    private String password = "goblin_secret";
    @JsonProperty
    private int maxPoolSize = 3;
    @JsonProperty
    private int minIdleConnections = 1;
    @JsonProperty
    private String sqliteDataBasePath = "/plugins/DummyPlugin/sqlite.db";

    public ModernDataBaseSetting() {

    }

    public Path sqliteDataBasePath() {
        return Path.of(sqliteDataBasePath);
    }

    public DatabaseDriver dataBaseDriver() {
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

    public String username() {
        return username;
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
