package de.unknowncity.astralib.common.configuration.setting.defaults;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class RedisSetting {
    @JsonProperty
    private final String host = "localhost";
    @JsonProperty
    private final int port = 6379;
    @JsonProperty
    private final String username = null;
    @JsonProperty
    private final String password = null;
    @JsonProperty
    private final boolean ssl = false;

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
