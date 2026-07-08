package de.unknowncity.astralib.common.platform;


import de.unknowncity.astralib.common.redis.RedisService;

public final class AstraLib {
    private static AstraPlatform platform;
    private static RedisService redisService;

    private AstraLib() {

    }

    public static void initialize(AstraPlatform platform) {
        AstraLib.platform = platform;
    }

    public static AstraPlatform getPlatform() {
        if (platform == null)
            throw new IllegalStateException("AstraLib wurde noch nicht initialisiert!");
        return platform;
    }

    public static RedisService getRedis() {
        if (redisService == null)
            throw new IllegalStateException("AstraLib wurde noch nicht initialisiert!");
        return redisService;
    }

    public static String getPlatformName() {
        return platform.getPlatformName();
    }
}
