package com.ramphalharrilal.inventory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ramphalharrilal.inventory.domain.InventoryItem;
import com.ramphalharrilal.inventory.domain.StockStatus;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InventoryServiceTest {
    private InventoryService service;

    @BeforeEach
    void setUp() {
        List<InventoryItem> items = List.of(
            item("HW-2", "Claw Hammer", 3, 5, "20.00"),
            item("HW-1", "Cordless Drill", 10, 4, "100.00"),
            item("HW-3", "Paint Brush", 0, 3, "10.00"));
        service = new InventoryService(() -> items);
    }

    @Test
    void combinesTextSearchAndStatusFilter() {
        var results = service.search("hammer", StockStatus.LOW_STOCK);
        assertEquals(List.of("HW-2"), results.stream().map(InventoryItem::sku).toList());
    }

    @Test
    void calculatesOperationalSummary() {
        var summary = service.summarize(service.search("", null));
        assertEquals(3, summary.skuCount());
        assertEquals(13, summary.unitCount());
        assertEquals(1, summary.lowStockCount());
        assertEquals(1, summary.outOfStockCount());
        assertEquals(new BigDecimal("1060.00"), summary.inventoryCost());
    }

    private static InventoryItem item(
            String sku, String name, int quantity, int reorderLevel, String cost) {
        BigDecimal costPrice = new BigDecimal(cost);
        return new InventoryItem(
            sku, name, "Hardware", "Demo Brand", costPrice,
            costPrice.add(new BigDecimal("15.00")), quantity, reorderLevel);
    }
}
