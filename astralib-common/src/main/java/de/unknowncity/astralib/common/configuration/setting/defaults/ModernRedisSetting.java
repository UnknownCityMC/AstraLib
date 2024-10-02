package de.unknowncity.astralib.common.configuration.setting.defaults;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModernRedisSetting {
    @JsonProperty
    private String host = "localhost";
    @JsonProperty
    private int port = 11;
    @JsonProperty
    private String password = "dwarf_secret";

    public ModernRedisSetting() {

    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public String password() {
        return password;
    }
}
