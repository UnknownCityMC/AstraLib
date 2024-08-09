package de.unknowncity.astralib.common.util;

import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static java.time.temporal.ChronoUnit.SECONDS;

public class UUIDFetcher {
    private static final HashMap<String, UUID> UUID_CACHE = new HashMap<>();
    private static final HashMap<UUID, String> NAME_CACHE = new HashMap<>();

    public static CompletableFuture<Optional<UUID>> fetchUUID(String name) {
        if (UUID_CACHE.containsKey(name)) {
            return CompletableFuture.completedFuture(Optional.of(UUID_CACHE.get(name)));
        }
        var request = HttpRequest.newBuilder(URI.create("https://api.mojang.com/users/profiles/minecraft/" + name))
                .header("Content-Type", "application/json")
                .GET()
                .timeout(Duration.of(10, SECONDS))
                .build();
        var response = HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString());
        return response.thenApply(stringHttpResponse -> {
            try (var jsonReader = new JsonReader(new InputStreamReader(new ByteArrayInputStream(stringHttpResponse.body().getBytes())))) {
                jsonReader.setLenient(true);
                var json = JsonParser.parseReader(jsonReader).getAsJsonObject().get("id");

                if (json == null) {
                    return Optional.empty();
                }
                var uuid = UUID.fromString(json.getAsString().replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
                UUID_CACHE.put(name, uuid);
                return Optional.of(uuid);
            } catch (IOException e) {
                return Optional.empty();
            }

        });
    }

    public static CompletableFuture<Optional<String>> fetchName(UUID uuid) {
        if (NAME_CACHE.containsKey(uuid)) {
            return CompletableFuture.completedFuture(Optional.of(NAME_CACHE.get(uuid)));
        }
        var request = HttpRequest.newBuilder(URI.create("https://sessionserver.mojang.com/session/minecraft/profile" + uuid))
                .header("Content-Type", "application/json")
                .GET()
                .timeout(Duration.of(10, SECONDS))
                .build();

        var response = HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString());
        return response.thenApply(stringHttpResponse -> {
            try (var jsonReader = new JsonReader(new InputStreamReader(new ByteArrayInputStream(stringHttpResponse.body().getBytes())))) {
                jsonReader.setLenient(true);
                var json = JsonParser.parseReader(jsonReader).getAsJsonObject().get("name");

                if (json == null) {
                    return Optional.empty();
                }
                var name = json.getAsString();
                NAME_CACHE.put(uuid, name);
                return Optional.of(name);
            } catch (IOException e) {
                return Optional.empty();
            }
        });
    }

    public static void clearCache() {
        UUID_CACHE.clear();
        NAME_CACHE.clear();
    }
}