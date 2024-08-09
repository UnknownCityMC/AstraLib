package de.unknowncity.astralib.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AstraMathTest {
    @Test
    void isBetween() {
        assertTrue(AstraMath.isBetween(2, 1, 3));
        assertTrue(AstraMath.isBetween(2, -1, 3));
        assertTrue(AstraMath.isBetween(2, 4, -3));
        assertTrue(AstraMath.isBetween(2, 3, 1));
    }
}