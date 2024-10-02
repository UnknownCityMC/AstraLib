package de.unknowncity.astralib.paper.api.message.color;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LegacyChatColorTest {

    @Test
    void testTranslateToMiniMessage() {
        assertEquals("<white><b>Test <red>!", LegacyChatColor.translateToMiniMessage('&', "&f&lTest <red>!"));
    }
}