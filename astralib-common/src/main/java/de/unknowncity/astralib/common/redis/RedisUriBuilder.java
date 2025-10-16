package de.unknowncity.astralib.common.redis;

import de.unknowncity.astralib.common.configuration.setting.defaults.RedisSetting;

public class RedisUriBuilder {

    /**
     * Builds a redis url from params
     *
     * @param host     Redis Hostname, e.g. "localhost"
     * @param port     Redis Port, e.g. 6379
     * @param user     Username,
     * @param password Passwort
     * @param ssl      true = rediss://, false = redis://
     * @return Redis URI as string
     */
    public static String build(String host, int port, String user, String password, boolean ssl) {
        String scheme = ssl ? "rediss://" : "redis://";
        StringBuilder sb = new StringBuilder(scheme);

        if (user != null && !user.isEmpty()) {
            sb.append(user);
            if (password != null && !password.isEmpty()) {
                sb.append(":").append(password);
            }
            sb.append("@");
        } else if (password != null && !password.isEmpty()) {
            sb.append(":").append(password).append("@");
        }

        sb.append(host).append(":").append(port);
        return sb.toString();
    }

    public static String build(RedisSetting redisSetting) {
        return build(redisSetting.host(), redisSetting.port(), redisSetting.username(), redisSetting.password(), redisSetting.ssl());
    }
}
