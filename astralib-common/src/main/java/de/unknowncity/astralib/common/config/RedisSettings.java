package de.unknowncity.astralib.common.config;

import java.util.Objects;

public final class RedisSettings {
    private final String host;
    private final int port;
    private final String user;
    private final String password;
    private final boolean ssl;

    public RedisSettings() {
        this.host = "localhost";
        this.port = 6379;
        this.user = "";
        this.password = "";
        this.ssl = false;
    }

    public RedisSettings(String host, int port, String user, String password, boolean ssl) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.ssl = ssl;
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public String user() {
        return user;
    }

    public String password() {
        return password;
    }

    public boolean ssl() {
        return ssl;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (RedisSettings) obj;
        return Objects.equals(this.host, that.host) &&
                this.port == that.port &&
                Objects.equals(this.user, that.user) &&
                Objects.equals(this.password, that.password) &&
                this.ssl == that.ssl;
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, user, password, ssl);
    }

    @Override
    public String toString() {
        return "RedisSettings[" +
                "host=" + host + ", " +
                "port=" + port + ", " +
                "user=" + user + ", " +
                "password=" + password + ", " +
                "ssl=" + ssl + ']';
    }


}
