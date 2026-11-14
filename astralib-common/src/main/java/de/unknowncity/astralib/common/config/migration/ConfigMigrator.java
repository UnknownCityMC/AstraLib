package de.unknowncity.astralib.common.config.migration;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.yaml.YAMLMapper;
import tools.jackson.dataformat.yaml.YAMLWriteFeature;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Migrates the config files of a plugin to the latest layout before they are loaded.
 * The current version is stored in a separate file, so it does not get edited by accident.
 * <pre>{@code
 * ConfigMigrator.builder(getDataPath(), getLogger())
 *         .migration(new V1RenameDatabaseKeys())
 *         .migration(ConfigMigration.of(2, "Move messages", context -> ...))
 *         .build()
 *         .migrate();
 * }</pre>
 */
public class ConfigMigrator {
    private final Path dataPath;
    private final Logger logger;
    private final ObjectMapper defaultMapper;
    private final Map<String, ObjectMapper> mappers;
    private final List<ConfigMigration> migrations;
    private final String versionFileName;
    private final String freshInstallMarker;
    private final String header;
    private final int latestVersion;

    private ConfigMigrator(Builder builder) {
        this.dataPath = builder.dataPath;
        this.logger = builder.logger;
        this.defaultMapper = builder.defaultMapper != null ? builder.defaultMapper : defaultMapper();
        this.mappers = Map.copyOf(builder.mappers);
        this.migrations = builder.migrations.stream()
                .sorted(Comparator.comparingInt(ConfigMigration::version))
                .toList();
        this.versionFileName = builder.versionFileName;
        this.freshInstallMarker = builder.freshInstallMarker;
        this.header = builder.header;
        this.latestVersion = migrations.isEmpty() ? 0 : migrations.getLast().version();
    }

    /**
     * Creates a new builder
     * @param dataPath the plugins data folder, all file names are resolved against it
     * @param logger the logger used to report progress
     * @return the builder
     */
    public static Builder builder(Path dataPath, Logger logger) {
        return new Builder(dataPath, logger);
    }

    /**
     * Applies all pending migrations. Call this before loading any configs.
     * <p>
     * If neither the version file nor the fresh install marker exist, the plugin is considered freshly installed
     * and the version is set to {@link #latestVersion()} without running any migration.
     * If only the version file is missing, the configs are considered to be from before migrations were introduced (version {@code 0}).
     */
    public void migrate() {
        var versionFile = dataPath.resolve(versionFileName);

        if (!Files.exists(versionFile)) {
            if (!Files.exists(dataPath.resolve(freshInstallMarker))) {
                writeVersion(versionFile, latestVersion);
                return;
            }
            writeVersion(versionFile, 0);
        }

        var currentVersion = readVersion(versionFile);

        if (currentVersion > latestVersion) {
            logger.warning("Config version " + currentVersion + " is newer than supported version " + latestVersion + ". Was the plugin downgraded?");
            return;
        }

        for (var migration : migrations) {
            if (migration.version() <= currentVersion) {
                continue;
            }
            logger.info("Migrating config from version " + currentVersion + " to " + migration.version() + ": " + migration.description());
            migration.migrate(new MigrationContext(dataPath, defaultMapper, mappers, logger, currentVersion, migration.version()));
            writeVersion(versionFile, migration.version());
            currentVersion = migration.version();
        }
    }

    /**
     * Get the version the configs will have after all migrations ran
     * @return the latest version
     */
    public int latestVersion() {
        return latestVersion;
    }

    private int readVersion(Path versionFile) {
        try {
            return Files.readAllLines(versionFile).stream()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .findFirst()
                    .map(Integer::parseInt)
                    .orElse(0);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + versionFileName, e);
        } catch (NumberFormatException e) {
            throw new IllegalStateException(versionFileName + " is corrupted. Please restore it or delete it if your config is up to date.", e);
        }
    }

    private void writeVersion(Path versionFile, int version) {
        try {
            Files.createDirectories(versionFile.getParent());
            Files.writeString(versionFile, "# " + header + "\n" + version + "\n");
        } catch (IOException e) {
            throw new UncheckedIOException("Could not write " + versionFileName, e);
        }
    }

    private static YAMLMapper defaultMapper() {
        return YAMLMapper.builder()
                .disable(YAMLWriteFeature.USE_NATIVE_TYPE_ID, YAMLWriteFeature.WRITE_DOC_START_MARKER)
                .build();
    }

    public static final class Builder {
        private final Path dataPath;
        private final Logger logger;
        private final List<ConfigMigration> migrations = new ArrayList<>();
        private final Map<String, ObjectMapper> mappers = new HashMap<>();
        private ObjectMapper defaultMapper;
        private String versionFileName = ".cfv";
        private String freshInstallMarker = "config.yml";
        private String header = "Used to migrate your configs automatically. DO NOT EDIT!";

        private Builder(Path dataPath, Logger logger) {
            this.dataPath = Objects.requireNonNull(dataPath, "dataPath");
            this.logger = Objects.requireNonNull(logger, "logger");
        }

        /**
         * Registers a migration. The order of registration does not matter.
         * @param migration the migration
         * @return this builder
         */
        public Builder migration(ConfigMigration migration) {
            migrations.add(Objects.requireNonNull(migration, "migration"));
            return this;
        }

        /**
         * Registers multiple migrations
         * @param migrations the migrations
         * @return this builder
         */
        public Builder migrations(ConfigMigration... migrations) {
            for (var migration : migrations) {
                migration(migration);
            }
            return this;
        }

        /**
         * Sets the name of the file the current version is stored in. Defaults to {@code .cfv}
         * @param versionFileName the file name, relative to the data folder
         * @return this builder
         */
        public Builder versionFile(String versionFileName) {
            this.versionFileName = Objects.requireNonNull(versionFileName, "versionFileName");
            return this;
        }

        /**
         * Sets the file whose absence marks a fresh install. Defaults to {@code config.yml}
         * @param freshInstallMarker the file name, relative to the data folder
         * @return this builder
         */
        public Builder freshInstallMarker(String freshInstallMarker) {
            this.freshInstallMarker = Objects.requireNonNull(freshInstallMarker, "freshInstallMarker");
            return this;
        }

        /**
         * Sets the comment written at the top of the version file
         * @param header the comment, without leading {@code #}
         * @return this builder
         */
        public Builder header(String header) {
            this.header = Objects.requireNonNull(header, "header");
            return this;
        }

        /**
         * Sets the mapper used for files without a mapper registered for their extension.
         * Defaults to a {@link YAMLMapper}. Pass e.g. a {@code JsonMapper} or {@code TomlMapper} to use another format.
         * @param mapper the mapper
         * @return this builder
         */
        public Builder mapper(ObjectMapper mapper) {
            this.defaultMapper = Objects.requireNonNull(mapper, "mapper");
            return this;
        }

        /**
         * Registers a mapper for all files with the given extension, for plugins that use multiple formats
         * <pre>{@code
         * builder.mapper("json", new JsonMapper())
         *        .mapper("toml", new TomlMapper());
         * }</pre>
         * @param extension the file extension, with or without leading dot, case-insensitive
         * @param mapper the mapper
         * @return this builder
         */
        public Builder mapper(String extension, ObjectMapper mapper) {
            Objects.requireNonNull(extension, "extension");
            var normalized = (extension.startsWith(".") ? extension.substring(1) : extension).toLowerCase(Locale.ROOT);
            mappers.put(normalized, Objects.requireNonNull(mapper, "mapper"));
            return this;
        }

        /**
         * Builds the migrator
         * @return the migrator
         * @throws IllegalArgumentException if a migration has a version below 1 or two migrations share a version
         */
        public ConfigMigrator build() {
            var versions = new HashSet<Integer>();
            for (var migration : migrations) {
                if (migration.version() < 1) {
                    throw new IllegalArgumentException("Migration version must be at least 1: " + migration.description());
                }
                if (!versions.add(migration.version())) {
                    throw new IllegalArgumentException("Duplicate migration version " + migration.version());
                }
            }
            return new ConfigMigrator(this);
        }
    }
}
