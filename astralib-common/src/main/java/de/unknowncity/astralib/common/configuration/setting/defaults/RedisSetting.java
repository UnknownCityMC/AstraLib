package de.unknowncity.astralib.common.configuration.setting.defaults;

public final class RedisSetting {
    private final String host = "localhost";
    private final int port = 6379;
    private final String username = null;
    private final String password = null;
    private final boolean ssl = true;

    public RedisSetting() {

    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public boolean ssl() {
        return ssl;
    }
}
