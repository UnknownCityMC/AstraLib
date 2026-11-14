package de.unknowncity.astralib.common.config.migration;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Passed to a {@link ConfigMigration} while it runs. Provides access to the plugins data folder
 * and helpers to safely edit config files.
 */
public class MigrationContext {
    private final Path dataPath;
    private final ObjectMapper defaultMapper;
    private final Map<String, ObjectMapper> mappers;
    private final Logger logger;
    private final int fromVersion;
    private final int toVersion;

    MigrationContext(Path dataPath, ObjectMapper defaultMapper, Map<String, ObjectMapper> mappers, Logger logger, int fromVersion, int toVersion) {
        this.dataPath = dataPath;
        this.defaultMapper = defaultMapper;
        this.mappers = mappers;
        this.logger = logger;
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
    }

    /**
     * Reads the given file, lets the editor modify its root node and writes it back.
     * The format is picked by {@link #mapperFor(String)}.
     * A backup of the original file is created first. Missing files are skipped.
     * @param fileName the file, relative to the plugins data folder
     * @param editor modifies the root node of the file
     */
    public void edit(String fileName, Consumer<ObjectNode> editor) {
        edit(fileName, mapperFor(fileName), editor);
    }

    /**
     * Reads the given file with the given mapper, lets the editor modify its root node and writes it back.
     * A backup of the original file is created first. Missing files are skipped.
     * @param fileName the file, relative to the plugins data folder
     * @param mapper the mapper used to read and write the file, determines the format
     * @param editor modifies the root node of the file
     */
    public void edit(String fileName, ObjectMapper mapper, Consumer<ObjectNode> editor) {
        var file = dataPath.resolve(fileName);
        if (!Files.exists(file)) {
            return;
        }

        backup(fileName);

        try {
            var tree = mapper.readTree(file.toFile());
            if (!(tree instanceof ObjectNode root)) {
                logger.warning("Skipping migration of " + fileName + ": root is not an object");
                return;
            }

            editor.accept(root);
            mapper.writeValue(file.toFile(), root);
        } catch (JacksonException e) {
            throw new IllegalStateException("Could not migrate " + fileName, e);
        }
    }

    /**
     * Copies the given file to {@code <fileName>.v<fromVersion>.bak}, unless that backup already exists.
     * Missing files are skipped. {@link #edit(String, Consumer)} does this automatically.
     * @param fileName the file, relative to the plugins data folder
     */
    public void backup(String fileName) {
        var file = dataPath.resolve(fileName);
        if (!Files.exists(file)) {
            return;
        }

        try {
            var backup = dataPath.resolve(fileName + ".v" + fromVersion + ".bak");
            if (!Files.exists(backup)) {
                Files.copy(file, backup, StandardCopyOption.COPY_ATTRIBUTES);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not back up " + fileName, e);
        }
    }

    /**
     * Get the plugins data folder, for migrations that need to move or delete files
     * @return the data folder
     */
    public Path dataPath() {
        return dataPath;
    }

    /**
     * Get the mapper registered for the extension of the given file, or the default mapper if there is none
     * @param fileName the file name
     * @return the mapper for the file
     */
    public ObjectMapper mapperFor(String fileName) {
        var dot = fileName.lastIndexOf('.');
        if (dot == -1) {
            return defaultMapper;
        }
        return mappers.getOrDefault(fileName.substring(dot + 1).toLowerCase(Locale.ROOT), defaultMapper);
    }

    public Logger logger() {
        return logger;
    }

    /**
     * Get the config version before this migration
     * @return the source version
     */
    public int fromVersion() {
        return fromVersion;
    }

    /**
     * Get the config version this migration migrates to
     * @return the target version
     */
    public int toVersion() {
        return toVersion;
    }
}
