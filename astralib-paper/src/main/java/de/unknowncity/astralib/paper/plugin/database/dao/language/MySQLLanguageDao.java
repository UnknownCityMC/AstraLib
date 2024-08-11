package de.unknowncity.astralib.paper.plugin.database.dao.language;

import de.chojo.sadu.queries.api.call.Call;
import de.chojo.sadu.queries.api.query.Query;
import de.chojo.sadu.queries.call.adapter.UUIDAdapter;
import de.unknowncity.astralib.common.message.lang.Language;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MySQLLanguageDao implements LanguageDao {

    @Override
    public CompletableFuture<Optional<Language>> read(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> Query.query("SELECT lang FROM language WHERE player_uuid = :player_uuid")
                .single(Call.of().bind("player_uuid", uuid, UUIDAdapter.AS_STRING))
                .map(row -> new Language(row.getString("lang")))
                .first());
    }

    @Override
    public CompletableFuture<Boolean> write(UUID uuid, Language language) {
        return CompletableFuture.supplyAsync(() -> Query.query("INSERT INTO language(player_uuid, lang) VALUES(:player_uuid, :lang)")
                .single(Call.of().bind("player_uuid", uuid, UUIDAdapter.AS_STRING).bind("lang", language.langIdentifier()))
                .insert()
                .changed());
    }

    @Override
    public CompletableFuture<Boolean> update(UUID uuid, Language language) {
        return CompletableFuture.supplyAsync(() -> Query.query("UPDATE language SET lang = :lang WHERE player_uuid = :player_uuid")
                .single(Call.of().bind("player_uuid", uuid, UUIDAdapter.AS_STRING).bind("lang", language.langIdentifier()))
                .update()
                .changed());
    }

    @Override
    public CompletableFuture<Boolean> delete(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> Query.query("DELETE FROM language WHERE player_uuid = :player_uuid")
                .single(Call.of().bind("player_uuid", uuid, UUIDAdapter.AS_STRING))
                .delete()
                .changed());
    }
}
