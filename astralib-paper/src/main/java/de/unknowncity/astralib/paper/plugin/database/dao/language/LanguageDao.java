package de.unknowncity.astralib.paper.plugin.database.dao.language;

import de.unknowncity.astralib.common.message.lang.Language;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface LanguageDao {

    CompletableFuture<Optional<Language>> read(UUID uuid);

    CompletableFuture<Boolean> write(UUID uuid, Language language);

    CompletableFuture<Boolean> update(UUID uuid, Language language);

    CompletableFuture<Boolean> delete(UUID uuid);
}
