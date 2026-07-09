package de.unknowncity.astralib.common.redis;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.unknowncity.astralib.common.platform.AstraLib;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class RedisService {

    private final Gson gson = new Gson();

    private final RedisClient client;
    private final StatefulRedisConnection<String, String> connection;
    private final RedisAsyncCommands<String, String> asyncCommands;
    private final StatefulRedisPubSubConnection<String, String> pubSubConnection;

    private final Map<String, List<ChannelSubscription<?>>> listeners = new ConcurrentHashMap<>();

    /**
     * Deserializes the channel json into a correctly typed {@link RedisMessage}
     * so payloads don't degrade to untyped maps
     */
    private record ChannelSubscription<T>(Class<T> payloadType, Consumer<RedisMessage<T>> handler) {
        void handle(Gson gson, String messageJson) {
            var messageType = TypeToken.getParameterized(RedisMessage.class, payloadType).getType();
            RedisMessage<T> message = gson.fromJson(messageJson, messageType);
            handler.accept(message);
        }
    }

    public RedisService(String redisUri) {
        try {
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
                    var subscriptions = listeners.get(channel);
                    if (subscriptions == null) {
                        return;
                    }
                    subscriptions.forEach(subscription ->
                            AstraLib.getPlatform().runSync(() -> subscription.handle(gson, messageJson))
                    );
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void publish(String channel, RedisMessage<T> message) {
        String json = gson.toJson(message);
        asyncCommands.publish(channel, json).exceptionally(ex -> {
            AstraLib.getPlatform().getLogger()
                    .warning("Redis publish failed on channel " + channel + ": " + ex.getMessage());
            return null;
        });
    }

    /**
     * Subscribes to a channel. A channel supports any number of subscribers.
     * @param channel the channel to listen on
     * @param payloadType the payload class the messages json gets deserialized into
     * @param handler called for every message on the platforms scheduler
     */
    public <T> void subscribe(String channel, Class<T> payloadType, Consumer<RedisMessage<T>> handler) {
        listeners.computeIfAbsent(channel, c -> new CopyOnWriteArrayList<>())
                .add(new ChannelSubscription<>(payloadType, handler));
        pubSubConnection.async().subscribe(channel);
    }

    /**
     * Removes all subscriptions of a channel and stops listening on it
     */
    public void unsubscribe(String channel) {
        listeners.remove(channel);
        pubSubConnection.async().unsubscribe(channel);
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

    /**
     * Blocks the calling thread until redis answers.
     * Avoid on server main threads, prefer {@link #getAsync(String)}
     */
    public String get(String key) {
        return connection.sync().get(key);
    }

    public CompletableFuture<String> getAsync(String key) {
        return asyncCommands.get(key).toCompletableFuture();
    }

    public void delete(String key) {
        asyncCommands.del(key);
    }

    /**
     * Blocks the calling thread until redis answers.
     * Avoid on server main threads, prefer {@link #existsAsync(String)}
     */
    public boolean exists(String key) {
        return connection.sync().exists(key) > 0;
    }

    public CompletableFuture<Boolean> existsAsync(String key) {
        return asyncCommands.exists(key).toCompletableFuture().thenApply(count -> count > 0);
    }

    public void shutdown() {
        pubSubConnection.close();
        connection.close();
        client.shutdown();
    }
}
