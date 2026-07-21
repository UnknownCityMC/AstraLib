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

    public static void initialize(AstraPlatform platform, RedisService redisService) {
        AstraLib.platform = platform;
        AstraLib.redisService = redisService;
    }

    public static boolean isRedisAvailable() {
        return redisService != null;
    }

    public static AstraPlatform getPlatform() {
        if (platform == null)
            throw new IllegalStateException("AstraLib has not been initialized yet!");
        return platform;
    }

    public static RedisService getRedis() {
        if (redisService == null)
            throw new IllegalStateException("Redis is not available! Check AstraLib.isRedisAvailable() before accessing it.");
        return redisService;
    }

    public static String getPlatformName() {
        return getPlatform().getPlatformName();
    }
}
