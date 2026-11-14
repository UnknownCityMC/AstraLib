package de.unknowncity.astralib.common.config;

import de.unknowncity.astralib.common.database.DatabaseDriver;

public class DatabaseSettings {
    private DatabaseDriver driver;
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private int maxPoolSize = 3;
    private int minIdleConnections = 1;

    public DatabaseSettings() {
        this.driver = DatabaseDriver.MARIADB;
        this.host = "localhost";
        this.port = 3306;
        this.database = "";
        this.username = "";
        this.password = "";
    }

    public DatabaseSettings(DatabaseDriver driver, String host, int port, String database, String username, String password, int maxPoolSize, int minIdleConnections) {
        this.driver = driver;
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.maxPoolSize = maxPoolSize;
        this.minIdleConnections = minIdleConnections;
    }

    public DatabaseSettings(DatabaseDriver driver, String host, int port, String database, String username, String password) {
        this.driver = driver;
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public DatabaseDriver driver() {
        return driver;
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
