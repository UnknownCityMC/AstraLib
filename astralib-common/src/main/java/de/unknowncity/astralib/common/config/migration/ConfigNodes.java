package de.unknowncity.astralib.common.config.migration;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

/**
 * Helpers for common edits on a config tree, meant to be used inside {@link MigrationContext#edit}.
 * <p>
 * Keys are addressed by dot separated paths, e.g. {@code database.driver}. Keys that contain a dot themselves
 * cannot be addressed this way, use the {@link ObjectNode} methods directly for those.
 * <pre>{@code
 * context.edit("config.yml", root -> {
 *     ConfigNodes.move(root, "database.dataBaseDriver", "database.driver");
 *     ConfigNodes.remove(root, "database.sqliteDataBasePath");
 *     ConfigNodes.add(root, "database.maxPoolSize", 3);
 * });
 * }</pre>
 */
public final class ConfigNodes {
    private static final JsonMapper MAPPER = new JsonMapper();

    private ConfigNodes() {
    }

    /**
     * Get the node at the given path
     * @param root the root node
     * @param path the dot separated path
     * @return the node, or {@code null} if it does not exist
     */
    public static JsonNode get(ObjectNode root, String path) {
        var parent = parent(root, path, false);
        return parent == null ? null : parent.get(lastKey(path));
    }

    /**
     * Checks whether a node exists at the given path
     * @param root the root node
     * @param path the dot separated path
     * @return whether the node exists
     */
    public static boolean has(ObjectNode root, String path) {
        return get(root, path) != null;
    }

    /**
     * Adds a key if it does not exist yet. Existing values are never overwritten, so user settings are kept.
     * Missing parent sections are created.
     * @param root the root node
     * @param path the dot separated path
     * @param value the value, either a {@link JsonNode} or any value jackson can convert (strings, numbers, lists, maps, ...)
     * @return {@code true} if the key was added, {@code false} if it already existed or a parent is not a section
     */
    public static boolean add(ObjectNode root, String path, Object value) {
        var parent = parent(root, path, true);
        if (parent == null || parent.has(lastKey(path))) {
            return false;
        }
        parent.set(lastKey(path), toNode(value));
        return true;
    }

    /**
     * Sets a key, overwriting any existing value. Missing parent sections are created.
     * @param root the root node
     * @param path the dot separated path
     * @param value the value, either a {@link JsonNode} or any value jackson can convert
     * @return {@code true} if the key was set, {@code false} if a parent is not a section
     */
    public static boolean set(ObjectNode root, String path, Object value) {
        var parent = parent(root, path, true);
        if (parent == null) {
            return false;
        }
        parent.set(lastKey(path), toNode(value));
        return true;
    }

    /**
     * Removes a key
     * @param root the root node
     * @param path the dot separated path
     * @return the removed node, or {@code null} if it did not exist
     */
    public static JsonNode remove(ObjectNode root, String path) {
        var parent = parent(root, path, false);
        return parent == null ? null : parent.remove(lastKey(path));
    }

    /**
     * Moves or renames a key. The old key is always removed.
     * If the target already exists, it is kept and the old value is dropped.
     * @param root the root node
     * @param from the dot separated path of the old key
     * @param to the dot separated path of the new key
     * @return {@code true} if the value was moved to the new key
     */
    public static boolean move(ObjectNode root, String from, String to) {
        var value = remove(root, from);
        return value != null && add(root, to, value);
    }

    private static ObjectNode parent(ObjectNode root, String path, boolean create) {
        var keys = path.split("\\.");
        var current = root;
        for (int i = 0; i < keys.length - 1; i++) {
            var child = current.get(keys[i]);
            if (child instanceof ObjectNode section) {
                current = section;
            } else if (child == null && create) {
                current = current.putObject(keys[i]);
            } else {
                return null;
            }
        }
        return current;
    }

    private static String lastKey(String path) {
        return path.substring(path.lastIndexOf('.') + 1);
    }

    private static JsonNode toNode(Object value) {
        return value instanceof JsonNode node ? node : MAPPER.valueToTree(value);
    }
}
