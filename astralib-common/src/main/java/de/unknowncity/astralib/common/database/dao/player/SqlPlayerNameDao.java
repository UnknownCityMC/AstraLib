package de.unknowncity.astralib.common.database.dao.player;

import de.chojo.sadu.queries.api.call.Call;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.chojo.sadu.queries.call.adapter.UUIDAdapter;
import de.unknowncity.astralib.common.database.QueryConfigHolder;
import de.unknowncity.astralib.common.database.dao.DaoExecutor;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Player name dao using plain SQL that works on all supported databases
 * (MariaDB, PostgreSQL, SQLite)
 */
public class SqlPlayerNameDao extends QueryConfigHolder implements PlayerNameDao {

    public SqlPlayerNameDao(QueryConfiguration queryConfiguration) {
        super(queryConfiguration);
    }

    @Override
    public CompletableFuture<Optional<String>> readName(UUID uuid) {
        var queryString = "SELECT name FROM player_names WHERE player_uuid = :player_uuid";
        return CompletableFuture.supplyAsync(() -> config.query(queryString)
                .single(Call.of().bind("player_uuid", uuid, UUIDAdapter.AS_STRING))
                .map(row -> row.getString("name"))
                .first(), DaoExecutor.EXECUTOR);
    }

    @Override
    public CompletableFuture<Optional<UUID>> readUUID(String name) {
        var queryString = "SELECT player_uuid FROM player_names WHERE LOWER(name) = :name";
        return CompletableFuture.supplyAsync(() -> config.query(queryString)
                .single(Call.of().bind("name", name.toLowerCase()))
                .map(row -> UUID.fromString(row.getString("player_uuid")))
                .first(), DaoExecutor.EXECUTOR);
    }

    @Override
    public CompletableFuture<Boolean> write(UUID uuid, String name) {
        var queryString = "INSERT INTO player_names(player_uuid, name) VALUES(:player_uuid, :name)";
        return CompletableFuture.supplyAsync(() -> config.query(queryString)
                .single(Call.of().bind("player_uuid", uuid, UUIDAdapter.AS_STRING).bind("name", name))
                .insert()
                .changed(), DaoExecutor.EXECUTOR);
    }

    @Override
    public CompletableFuture<Boolean> update(UUID uuid, String name) {
        var queryString = "UPDATE player_names SET name = :name WHERE player_uuid = :player_uuid";
        return CompletableFuture.supplyAsync(() -> config.query(queryString)
                .single(Call.of().bind("player_uuid", uuid, UUIDAdapter.AS_STRING).bind("name", name))
                .update()
                .changed(), DaoExecutor.EXECUTOR);
    }
}
