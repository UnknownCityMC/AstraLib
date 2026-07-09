package de.unknowncity.astralib.common.util;

import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static java.time.temporal.ChronoUnit.SECONDS;

public class UUIDFetcher {
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.of(10, SECONDS))
            .build();
    private static final Map<String, UUID> UUID_CACHE = new ConcurrentHashMap<>();
    private static final Map<UUID, String> NAME_CACHE = new ConcurrentHashMap<>();

    public static CompletableFuture<Optional<UUID>> fetchUUID(String name) {
        var cached = UUID_CACHE.get(name);
        if (cached != null) {
            return CompletableFuture.completedFuture(Optional.of(cached));
        }
        var request = HttpRequest.newBuilder(URI.create("https://api.mojang.com/users/profiles/minecraft/" + name))
                .header("Content-Type", "application/json")
                .GET()
                .timeout(Duration.of(10, SECONDS))
                .build();
        return HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(response -> {
            try {
                var json = JsonParser.parseString(response.body()).getAsJsonObject().get("id");

                if (json == null) {
                    return Optional.empty();
                }
                var uuid = UUID.fromString(json.getAsString().replaceFirst(
                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
                UUID_CACHE.put(name, uuid);
                return Optional.of(uuid);
            } catch (RuntimeException e) {
                // 204/404 responses carry no json body
                return Optional.empty();
            }
        });
    }

    public static CompletableFuture<Optional<String>> fetchName(UUID uuid) {
        var cached = NAME_CACHE.get(uuid);
        if (cached != null) {
            return CompletableFuture.completedFuture(Optional.of(cached));
        }
        // The session server expects the uuid without dashes
        var request = HttpRequest.newBuilder(URI.create(
                        "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "")))
                .header("Content-Type", "application/json")
                .GET()
                .timeout(Duration.of(10, SECONDS))
                .build();

        return HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(response -> {
            try {
                var json = JsonParser.parseString(response.body()).getAsJsonObject().get("name");

                if (json == null) {
                    return Optional.empty();
                }
                var name = json.getAsString();
                NAME_CACHE.put(uuid, name);
                return Optional.of(name);
            } catch (RuntimeException e) {
                // 204/404 responses carry no json body
                return Optional.empty();
            }
        });
    }

    public static void clearCache() {
        UUID_CACHE.clear();
        NAME_CACHE.clear();
    }
}
