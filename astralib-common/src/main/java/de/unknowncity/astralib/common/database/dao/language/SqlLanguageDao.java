package de.unknowncity.astralib.common.database.dao.language;

import de.chojo.sadu.queries.api.call.Call;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.chojo.sadu.queries.call.adapter.UUIDAdapter;
import de.unknowncity.astralib.common.database.QueryConfigHolder;
import de.unknowncity.astralib.common.database.dao.DaoExecutor;
import de.unknowncity.astralib.common.message.lang.Language;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Language dao using plain SQL that works on all supported databases
 * (MariaDB, PostgreSQL, SQLite)
 */
public class SqlLanguageDao extends QueryConfigHolder implements LanguageDao {

    public SqlLanguageDao(QueryConfiguration queryConfiguration) {
        super(queryConfiguration);
    }

    @Override
    public CompletableFuture<Optional<Language>> read(UUID uuid) {
        var queryString = "SELECT lang FROM language WHERE player_uuid = :player_uuid";
        return CompletableFuture.supplyAsync(() -> config.query(queryString)
                .single(Call.of().bind("player_uuid", uuid, UUIDAdapter.AS_STRING))
                .map(row -> new Language(row.getString("lang")))
                .first(), DaoExecutor.EXECUTOR);
    }

    @Override
    public CompletableFuture<Boolean> write(UUID uuid, Language language) {
        var queryString = "INSERT INTO language(player_uuid, lang) VALUES(:player_uuid, :lang)";
        return CompletableFuture.supplyAsync(() -> config.query(queryString)
                .single(Call.of().bind("player_uuid", uuid, UUIDAdapter.AS_STRING).bind("lang", language.langIdentifier()))
                .insert()
                .changed(), DaoExecutor.EXECUTOR);
    }

    @Override
    public CompletableFuture<Boolean> update(UUID uuid, Language language) {
        var queryString = "UPDATE language SET lang = :lang WHERE player_uuid = :player_uuid";
        return CompletableFuture.supplyAsync(() -> config.query(queryString)
                .single(Call.of().bind("player_uuid", uuid, UUIDAdapter.AS_STRING).bind("lang", language.langIdentifier()))
                .update()
                .changed(), DaoExecutor.EXECUTOR);
    }

    @Override
    public CompletableFuture<Boolean> delete(UUID uuid) {
        var queryString = "DELETE FROM language WHERE player_uuid = :player_uuid";
        return CompletableFuture.supplyAsync(() -> config.query(queryString)
                .single(Call.of().bind("player_uuid", uuid, UUIDAdapter.AS_STRING))
                .delete()
                .changed(), DaoExecutor.EXECUTOR);
    }
}
