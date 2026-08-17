package com.ramphalharrilal.inventory.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class InventoryItemTest {
    @Test
    void calculatesStockStatusFromQuantityAndReorderLevel() {
        assertEquals(StockStatus.OUT_OF_STOCK, item(0, 4).status());
        assertEquals(StockStatus.LOW_STOCK, item(4, 4).status());
        assertEquals(StockStatus.IN_STOCK, item(5, 4).status());
    }

    @Test
    void searchesAcrossBusinessFieldsWithoutCaseSensitivity() {
        InventoryItem item = item(8, 4);
        assertTrue(item.matches("hw-1001"));
        assertTrue(item.matches("DRILL"));
        assertTrue(item.matches("power tools"));
        assertTrue(item.matches("buildpro"));
        assertFalse(item.matches("paint"));
    }

    @Test
    void rejectsNegativeQuantity() {
        assertThrows(IllegalArgumentException.class, () -> item(-1, 4));
    }

    private static InventoryItem item(int quantity, int reorderLevel) {
        return new InventoryItem(
            "HW-1001", "Cordless Drill", "Power Tools", "BuildPro",
            new BigDecimal("425.00"), new BigDecimal("599.00"), quantity, reorderLevel);
    }
}
