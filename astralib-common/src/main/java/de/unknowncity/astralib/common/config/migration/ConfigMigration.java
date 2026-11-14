package de.unknowncity.astralib.common.config.migration;

import java.util.function.Consumer;

/**
 * A single migration step that brings the config files of a plugin to {@code version()}.
 * <p>
 * Versions are plugin specific and must be unique and greater than {@code 0} within one {@link ConfigMigrator}.
 * Migrations are applied in ascending version order, each one at most once.
 */
public interface ConfigMigration {

    /**
     * Get the config version this migration migrates to
     * @return the target version
     */
    int version();

    /**
     * Get a short human-readable description, used for logging
     * @return the description
     */
    String description();

    /**
     * Performs the migration
     * @param context the context providing access to the plugins config files
     */
    void migrate(MigrationContext context);

    /**
     * Creates a migration from a lambda
     * <pre>{@code
     * ConfigMigration.of(2, "Rename prefix key", context ->
     *         context.edit("config.yml", root -> root.set("chat-prefix", root.remove("prefix"))));
     * }</pre>
     * @param version the target version
     * @param description a short description, used for logging
     * @param action the migration logic
     * @return the migration
     */
    static ConfigMigration of(int version, String description, Consumer<MigrationContext> action) {
        return new ConfigMigration() {
            @Override
            public int version() {
                return version;
            }

            @Override
            public String description() {
                return description;
            }

            @Override
            public void migrate(MigrationContext context) {
                action.accept(context);
            }
        };
    }
}
