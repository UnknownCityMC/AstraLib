package de.unknowncity.astralib.common.redis;

import com.google.gson.Gson;
import de.unknowncity.astralib.common.platform.AstraLib;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class RedisService {

    private final Gson gson = new Gson();

    private final RedisClient client;
    private final StatefulRedisConnection<String, String> connection;
    private final RedisAsyncCommands<String, String> asyncCommands;
    private final StatefulRedisPubSubConnection<String, String> pubSubConnection;

    private final Map<String, Consumer<RedisMessage<?>>> listeners = new ConcurrentHashMap<>();

    public RedisService(String redisUri) {
        client = RedisClient.create(redisUri);
        client.setOptions(io.lettuce.core.ClientOptions.builder()
                .autoReconnect(true)
                .pingBeforeActivateConnection(true)
                .build()
        );

        connection = client.connect();
        asyncCommands = connection.async();
        pubSubConnection = client.connectPubSub();

        pubSubConnection.addListener(new RedisPubSubListener<String, String>() {
            @Override
            public void message(String channel, String messageJson) {
                Consumer<RedisMessage<?>> listener = listeners.get(channel);
                if (listener != null) {
                    RedisMessage<?> msg = gson.fromJson(messageJson, RedisMessage.class);
                    AstraLib.getPlatform().runSync(() -> listener.accept(msg));
                }
            }

            @Override
            public void message(String pattern, String channel, String message) {
            }

            @Override
            public void subscribed(String channel, long count) {
            }

            @Override
            public void psubscribed(String pattern, long count) {
            }

            @Override
            public void unsubscribed(String channel, long count) {
            }

            @Override
            public void punsubscribed(String pattern, long count) {
            }
        });
    }

    public <T> void publish(String channel, RedisMessage<T> message) {
        String json = gson.toJson(message);
        asyncCommands.publish(channel, json).exceptionally(ex -> {
            AstraLib.getPlatform().getLogger()
                    .warning("Redis publish failed on channel " + channel + ": " + ex.getMessage());
            return null;
        });
    }

    public void subscribe(String channel, Consumer<RedisMessage<?>> handler) {
        listeners.put(channel, handler);
        pubSubConnection.async().subscribe(channel);
    }

    public void set(String key, String value, long ttlSeconds) {
        if (ttlSeconds > 0) {
            asyncCommands.setex(key, ttlSeconds, value);
        } else {
            asyncCommands.set(key, value);
        }
    }

    public void set(String key, String value) {
        set(key, value, 0);
    }

    public String get(String key) {
        return connection.sync().get(key);
    }

    public void delete(String key) {
        asyncCommands.del(key);
    }

    public boolean exists(String key) {
        return connection.sync().exists(key) > 0;
    }

    public void shutdown() {
        pubSubConnection.close();
        connection.close();
        client.shutdown();
    }
}
