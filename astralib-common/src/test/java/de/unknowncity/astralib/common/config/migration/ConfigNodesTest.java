package de.unknowncity.astralib.common.config.migration;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConfigNodesTest {
    private static final JsonMapper MAPPER = new JsonMapper();

    private ObjectNode tree(String json) {
        return (ObjectNode) MAPPER.readTree(json);
    }

    @Test
    void addCreatesSectionsAndKeepsExistingValues() {
        var root = tree("{\"a\": {\"b\": 1}}");
        assertTrue(ConfigNodes.add(root, "x.y.z", List.of("one", "two")));
        assertFalse(ConfigNodes.add(root, "a.b", 2));
        assertEquals(1, ConfigNodes.get(root, "a.b").asInt());
        assertEquals("two", ConfigNodes.get(root, "x.y.z").get(1).asString());
    }

    @Test
    void addFailsIfParentIsNoSection() {
        var root = tree("{\"a\": 1}");
        assertFalse(ConfigNodes.add(root, "a.b", 2));
        assertEquals(1, root.get("a").asInt());
    }

    @Test
    void removeReturnsRemovedNode() {
        var root = tree("{\"a\": {\"b\": 1}}");
        assertEquals(1, ConfigNodes.remove(root, "a.b").asInt());
        assertNull(ConfigNodes.remove(root, "a.b"));
        assertNull(ConfigNodes.remove(root, "missing.key"));
    }

    @Test
    void moveRenamesAndDoesNotOverwrite() {
        var root = tree("{\"db\": {\"old\": \"MARIADB\"}, \"keep\": 1, \"other\": 2}");
        assertTrue(ConfigNodes.move(root, "db.old", "database.driver"));
        assertEquals("MARIADB", ConfigNodes.get(root, "database.driver").asString());
        assertFalse(ConfigNodes.has(root, "db.old"));

        assertFalse(ConfigNodes.move(root, "other", "keep"));
        assertEquals(1, root.get("keep").asInt());
        assertFalse(root.has("other"));
    }
}
