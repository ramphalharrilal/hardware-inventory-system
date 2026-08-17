package com.ramphalharrilal.inventory.domain;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;

public record InventoryItem(
        String sku,
        String name,
        String category,
        String brand,
        BigDecimal costPrice,
        BigDecimal sellingPrice,
        int quantity,
        int reorderLevel) {

    public InventoryItem {
        sku = requireText(sku, "sku");
        name = requireText(name, "name");
        category = requireText(category, "category");
        brand = requireText(brand, "brand");
        costPrice = requireNonNegative(costPrice, "costPrice");
        sellingPrice = requireNonNegative(sellingPrice, "sellingPrice");
        if (quantity < 0) {
            throw new IllegalArgumentException("quantity cannot be negative");
        }
        if (reorderLevel < 0) {
            throw new IllegalArgumentException("reorderLevel cannot be negative");
        }
    }

    public StockStatus status() {
        if (quantity == 0) {
            return StockStatus.OUT_OF_STOCK;
        }
        if (quantity <= reorderLevel) {
            return StockStatus.LOW_STOCK;
        }
        return StockStatus.IN_STOCK;
    }

    public BigDecimal inventoryCost() {
        return costPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal potentialRevenue() {
        return sellingPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public boolean matches(String query) {
        if (query == null || query.isBlank()) {
            return true;
        }
        String normalized = query.strip().toLowerCase(Locale.ROOT);
        return String.join(" ", sku, name, category, brand)
            .toLowerCase(Locale.ROOT)
            .contains(normalized);
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " cannot be blank");
        }
        return value.strip();
    }

    private static BigDecimal requireNonNegative(BigDecimal value, String field) {
        Objects.requireNonNull(value, field + " cannot be null");
        if (value.signum() < 0) {
            throw new IllegalArgumentException(field + " cannot be negative");
        }
        return value;
    }
}
