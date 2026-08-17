package com.ramphalharrilal.inventory.domain;

import java.math.BigDecimal;

public record InventorySummary(
        int skuCount,
        int unitCount,
        long lowStockCount,
        long outOfStockCount,
        BigDecimal inventoryCost,
        BigDecimal potentialRevenue) {
}
