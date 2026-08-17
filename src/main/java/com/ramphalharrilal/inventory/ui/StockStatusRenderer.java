package com.ramphalharrilal.inventory.ui;

import com.ramphalharrilal.inventory.domain.InventoryItem;
import com.ramphalharrilal.inventory.domain.StockStatus;
import java.awt.Color;
import java.awt.Component;
import java.math.BigDecimal;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

final class StockStatusRenderer extends DefaultTableCellRenderer {
    private static final Color EVEN_ROW = new Color(248, 250, 252);
    private static final Color LOW_STOCK = new Color(255, 248, 218);
    private static final Color OUT_OF_STOCK = new Color(255, 230, 230);
    private final InventoryTableModel model;

    StockStatusRenderer(InventoryTableModel model) {
        this.model = model;
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean selected, boolean focused, int row, int column) {
        Object displayValue = value instanceof BigDecimal ? String.format("TT$ %,.2f", value) : value;
        Component component = super.getTableCellRendererComponent(
            table, displayValue, selected, focused, row, column);

        if (component instanceof JLabel label) {
            label.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 8, 0, 8));
            label.setHorizontalAlignment(column >= 4 && column <= 6 ? JLabel.RIGHT : JLabel.LEFT);
        }

        if (!selected) {
            InventoryItem item = model.getItem(table.convertRowIndexToModel(row));
            if (item.status() == StockStatus.OUT_OF_STOCK) {
                component.setBackground(OUT_OF_STOCK);
            } else if (item.status() == StockStatus.LOW_STOCK) {
                component.setBackground(LOW_STOCK);
            } else {
                component.setBackground(row % 2 == 0 ? Color.WHITE : EVEN_ROW);
            }
            component.setForeground(new Color(30, 41, 59));
        }
        return component;
    }
}
