package de.unknowncity.astralib.common.redis;

public record RedisMessage<T>(String type, T payload) {}
