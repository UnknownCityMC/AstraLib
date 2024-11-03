package de.unknowncity.astralib.paper.plugin.database.dao.language;

import de.chojo.sadu.queries.api.call.Call;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.chojo.sadu.queries.call.adapter.UUIDAdapter;
import de.unknowncity.astralib.common.message.lang.Language;
import de.unknowncity.astralib.common.database.QueryConfigHolder;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MariaDBLanguageDao extends QueryConfigHolder implements LanguageDao {

    public MariaDBLanguageDao(QueryConfiguration queryConfiguration) {
        super(queryConfiguration);
    }

    @Override
    public CompletableFuture<Optional<Language>> read(UUID uuid) {
        @org.intellij.lang.annotations.Language("mariadb")
        var queryString = "SELECT lang FROM language WHERE player_uuid = :player_uuid";
        return CompletableFuture.supplyAsync(() -> config.query(queryString)
                .single(Call.of().bind("player_uuid", uuid, UUIDAdapter.AS_STRING))
                .map(row -> new Language(row.getString("lang")))
                .first());
    }

    @Override
    public CompletableFuture<Boolean> write(UUID uuid, Language language) {
        @org.intellij.lang.annotations.Language("mariadb")
        var queryString = "INSERT INTO language(player_uuid, lang) VALUES(:player_uuid, :lang)";
        return CompletableFuture.supplyAsync(() -> config.query(queryString)
                .single(Call.of().bind("player_uuid", uuid, UUIDAdapter.AS_STRING).bind("lang", language.langIdentifier()))
                .insert()
                .changed());
    }

    @Override
    public CompletableFuture<Boolean> update(UUID uuid, Language language) {
        @org.intellij.lang.annotations.Language("mariadb")
        var queryString = "UPDATE language SET lang = :lang WHERE player_uuid = :player_uuid";
        return CompletableFuture.supplyAsync(() -> config.query("UPDATE language SET lang = :lang WHERE player_uuid = :player_uuid")
                .single(Call.of().bind("player_uuid", uuid, UUIDAdapter.AS_STRING).bind("lang", language.langIdentifier()))
                .update()
                .changed());
    }

    @Override
    public CompletableFuture<Boolean> delete(UUID uuid) {
        @org.intellij.lang.annotations.Language("mariadb")
        var queryString = "DELETE FROM language WHERE player_uuid = :player_uuid";
        return CompletableFuture.supplyAsync(() -> config.query("DELETE FROM language WHERE player_uuid = :player_uuid")
                .single(Call.of().bind("player_uuid", uuid, UUIDAdapter.AS_STRING))
                .delete()
                .changed());
    }
}
