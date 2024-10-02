package de.unknowncity.astralib.common.configuration.setting.defaults;

import de.unknowncity.astralib.common.configuration.setting.Setting;

/**
 * @deprecated version 0.4.0 introduces a new config system based on jackson
 * Use {@link ModernRedisSetting} instead
 */
@Deprecated(forRemoval = true, since = "0.4.0")
public class RedisSetting implements Setting {
    private String host = "localhost";
    private int port = 11;
    private String password = "dwarf_secret";

    public RedisSetting() {

    }

    public RedisSetting(String host, int port, String password) {
        this.host = host;
        this.port = port;
        this.password = password;
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