package de.unknowncity.astralib.common.service;

import de.unknowncity.astralib.common.database.dao.player.PlayerNameDao;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Caches uuid to name mappings of every player that ever joined,
 * backed by the database — no Mojang api calls required.
 * Populated automatically by the AstraLib platform plugins on join.
 */
public class PlayerNameService {
    private final PlayerNameDao playerNameDao;
    private final Map<UUID, String> nameCache = new ConcurrentHashMap<>();
    // Keyed by lowercase name for case-insensitive lookups
    private final Map<String, UUID> uuidCache = new ConcurrentHashMap<>();

    public PlayerNameService(PlayerNameDao playerNameDao) {
        this.playerNameDao = playerNameDao;
    }

    /**
     * Caches a player and persists the mapping, called on join by the platform plugins
     */
    public void cachePlayer(UUID uuid, String name) {
        var previous = nameCache.put(uuid, name);
        if (previous != null && !previous.equalsIgnoreCase(name)) {
            uuidCache.remove(previous.toLowerCase());
        }
        uuidCache.put(name.toLowerCase(), uuid);

        if (name.equals(previous)) {
            return;
        }
        playerNameDao.readName(uuid).thenAccept(stored -> {
            if (stored.isEmpty()) {
                playerNameDao.write(uuid, name);
            } else if (!stored.get().equals(name)) {
                playerNameDao.update(uuid, name);
            }
        });
    }

    /**
     * Cache-only lookup, no database access
     */
    public Optional<String> getCachedName(UUID uuid) {
        return Optional.ofNullable(nameCache.get(uuid));
    }

    /**
     * Cache-only lookup, no database access. Case-insensitive
     */
    public Optional<UUID> getCachedUUID(String name) {
        return Optional.ofNullable(uuidCache.get(name.toLowerCase()));
    }

    /**
     * Looks up the last known name of a player, cache first, then database
     */
    public CompletableFuture<Optional<String>> getName(UUID uuid) {
        var cached = getCachedName(uuid);
        if (cached.isPresent()) {
            return CompletableFuture.completedFuture(cached);
        }
        return playerNameDao.readName(uuid).thenApply(name -> {
            name.ifPresent(value -> {
                nameCache.put(uuid, value);
                uuidCache.put(value.toLowerCase(), uuid);
            });
            return name;
        });
    }

    /**
     * Looks up the uuid for a last known name, cache first, then database. Case-insensitive
     */
    public CompletableFuture<Optional<UUID>> getUUID(String name) {
        var cached = getCachedUUID(name);
        if (cached.isPresent()) {
            return CompletableFuture.completedFuture(cached);
        }
        return playerNameDao.readUUID(name).thenApply(uuid -> {
            uuid.ifPresent(value -> {
                nameCache.put(value, name);
                uuidCache.put(name.toLowerCase(), value);
            });
            return uuid;
        });
    }
}
