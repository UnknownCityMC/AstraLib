package de.unknowncity.astralib.common.database.dao.player;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerNameDao {

    CompletableFuture<Optional<String>> readName(UUID uuid);

    CompletableFuture<Optional<UUID>> readUUID(String name);

    CompletableFuture<Boolean> write(UUID uuid, String name);

    CompletableFuture<Boolean> update(UUID uuid, String name);
}
