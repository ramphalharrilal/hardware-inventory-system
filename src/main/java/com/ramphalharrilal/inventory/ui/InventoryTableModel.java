package com.ramphalharrilal.inventory.ui;

import com.ramphalharrilal.inventory.domain.InventoryItem;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

final class InventoryTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {
        "SKU", "Item", "Category", "Brand", "Cost (TTD)", "Price (TTD)", "Units", "Status"
    };
    private List<InventoryItem> items = new ArrayList<>();

    void setItems(List<InventoryItem> items) {
        this.items = new ArrayList<>(items);
        fireTableDataChanged();
    }

    InventoryItem getItem(int row) {
        return items.get(row);
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return switch (column) {
            case 4, 5 -> BigDecimal.class;
            case 6 -> Integer.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int row, int column) {
        InventoryItem item = items.get(row);
        return switch (column) {
            case 0 -> item.sku();
            case 1 -> item.name();
            case 2 -> item.category();
            case 3 -> item.brand();
            case 4 -> item.costPrice();
            case 5 -> item.sellingPrice();
            case 6 -> item.quantity();
            case 7 -> item.status().label();
            default -> "";
        };
    }
}
