package com.ramphalharrilal.inventory.repository;

import com.ramphalharrilal.inventory.domain.InventoryItem;
import java.util.List;

@FunctionalInterface
public interface InventoryRepository {
    List<InventoryItem> findAll();
}
