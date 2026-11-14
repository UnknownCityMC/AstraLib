package de.unknowncity.astralib.common.config.migration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class ConfigMigratorTest {
    private static final Logger LOGGER = Logger.getLogger("test");

    @TempDir
    Path dataPath;

    private ConfigMigrator migrator() {
        return ConfigMigrator.builder(dataPath, LOGGER)
                .migration(ConfigMigration.of(2, "Rename b", context ->
                        context.edit("config.yml", root -> root.set("c", root.remove("b")))))
                .migration(ConfigMigration.of(1, "Rename a", context ->
                        context.edit("config.yml", root -> root.set("b", root.remove("a")))))
                .build();
    }

    @Test
    void freshInstallSkipsMigrations() throws IOException {
        migrator().migrate();
        assertTrue(Files.readString(dataPath.resolve(".cfv")).contains("2"));
        assertFalse(Files.exists(dataPath.resolve("config.yml")));
    }

    @Test
    void legacyConfigIsMigratedInOrder() throws IOException {
        Files.writeString(dataPath.resolve("config.yml"), "a: 5\n");
        migrator().migrate();

        assertEquals("c: 5", Files.readString(dataPath.resolve("config.yml")).trim());
        assertEquals("a: 5", Files.readString(dataPath.resolve("config.yml.v0.bak")).trim());
        assertTrue(Files.exists(dataPath.resolve("config.yml.v1.bak")));
        assertTrue(Files.readString(dataPath.resolve(".cfv")).endsWith("2\n"));
    }

    @Test
    void mapperIsPickedByExtension() throws IOException {
        Files.writeString(dataPath.resolve("config.yml"), "a: 1\n");
        Files.writeString(dataPath.resolve("data.json"), "{\"a\": 1}");

        ConfigMigrator.builder(dataPath, LOGGER)
                .mapper(".JSON", new JsonMapper())
                .migration(ConfigMigration.of(1, "Rename a", context -> {
                    context.edit("config.yml", root -> ConfigNodes.move(root, "a", "b"));
                    context.edit("data.json", root -> ConfigNodes.move(root, "a", "b"));
                }))
                .build()
                .migrate();

        assertEquals("b: 1", Files.readString(dataPath.resolve("config.yml")).trim());
        assertEquals("{\"b\":1}", Files.readString(dataPath.resolve("data.json")).trim());
    }

    @Test
    void duplicateVersionsAreRejected() {
        var builder = ConfigMigrator.builder(dataPath, LOGGER)
                .migration(ConfigMigration.of(1, "a", context -> {}))
                .migration(ConfigMigration.of(1, "b", context -> {}));
        assertThrows(IllegalArgumentException.class, builder::build);
    }
}
