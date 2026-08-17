package com.ramphalharrilal.inventory.service;

import com.ramphalharrilal.inventory.domain.InventoryItem;
import com.ramphalharrilal.inventory.domain.InventorySummary;
import com.ramphalharrilal.inventory.domain.StockStatus;
import com.ramphalharrilal.inventory.repository.InventoryRepository;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

public final class InventoryService {
    private final InventoryRepository repository;

    public InventoryService(InventoryRepository repository) {
        this.repository = repository;
    }

    public List<InventoryItem> search(String query, StockStatus status) {
        return repository.findAll().stream()
            .filter(item -> item.matches(query))
            .filter(item -> status == null || item.status() == status)
            .sorted(Comparator.comparing(InventoryItem::name, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    public InventorySummary summarize(List<InventoryItem> items) {
        int units = items.stream().mapToInt(InventoryItem::quantity).sum();
        long lowStock = items.stream().filter(item -> item.status() == StockStatus.LOW_STOCK).count();
        long outOfStock = items.stream().filter(item -> item.status() == StockStatus.OUT_OF_STOCK).count();
        BigDecimal cost = items.stream()
            .map(InventoryItem::inventoryCost)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal revenue = items.stream()
            .map(InventoryItem::potentialRevenue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new InventorySummary(items.size(), units, lowStock, outOfStock, cost, revenue);
    }
}
