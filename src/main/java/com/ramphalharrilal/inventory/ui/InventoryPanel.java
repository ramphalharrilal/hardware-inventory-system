package com.ramphalharrilal.inventory.ui;

import com.ramphalharrilal.inventory.domain.InventoryItem;
import com.ramphalharrilal.inventory.domain.InventorySummary;
import com.ramphalharrilal.inventory.domain.StockStatus;
import com.ramphalharrilal.inventory.repository.InventoryDataException;
import com.ramphalharrilal.inventory.service.InventoryService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

public final class InventoryPanel extends JPanel {
    private static final Color NAVY = new Color(15, 42, 69);
    private static final Color GREEN = new Color(31, 122, 73);
    private static final Color PAGE = new Color(241, 245, 249);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy  h:mm a");

    private final InventoryService service;
    private final InventoryTableModel tableModel = new InventoryTableModel();
    private final JTextField searchField = new JTextField();
    private final JComboBox<String> statusFilter = new JComboBox<>(
        new String[] {"All statuses", "In Stock", "Low Stock", "Out of Stock"});
    private final JLabel skuValue = metricValue();
    private final JLabel unitValue = metricValue();
    private final JLabel lowStockValue = metricValue();
    private final JLabel inventoryValue = metricValue();
    private final JLabel resultsLabel = new JLabel();
    private final JLabel refreshLabel = new JLabel();

    public InventoryPanel(InventoryService service) {
        this.service = service;
        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));
        setBackground(PAGE);
        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
        refreshInventory();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(NAVY);
        header.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Retail Inventory Operations");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 25));
        JLabel subtitle = new JLabel("Search products, watch stock risk, and review inventory value");
        subtitle.setForeground(new Color(203, 213, 225));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        titles.add(title);
        titles.add(Box.createVerticalStrut(4));
        titles.add(subtitle);
        header.add(titles, BorderLayout.CENTER);

        JLabel demoBadge = new JLabel("PUBLIC DEMO", SwingConstants.CENTER);
        demoBadge.setOpaque(true);
        demoBadge.setBackground(GREEN);
        demoBadge.setForeground(Color.WHITE);
        demoBadge.setFont(new Font("SansSerif", Font.BOLD, 12));
        demoBadge.setBorder(BorderFactory.createEmptyBorder(9, 12, 9, 12));
        header.add(demoBadge, BorderLayout.EAST);
        return header;
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setOpaque(false);
        content.add(createMetrics(), BorderLayout.NORTH);
        content.add(createTableArea(), BorderLayout.CENTER);
        return content;
    }

    private JPanel createMetrics() {
        JPanel metrics = new JPanel(new GridLayout(1, 4, 12, 0));
        metrics.setOpaque(false);
        metrics.add(metricCard("ACTIVE SKUS", skuValue, "Products in this view"));
        metrics.add(metricCard("UNITS ON HAND", unitValue, "Across visible products"));
        metrics.add(metricCard("STOCK RISKS", lowStockValue, "Low or out of stock"));
        metrics.add(metricCard("INVENTORY COST", inventoryValue, "Fictional TTD sample data"));
        return metrics;
    }

    private JPanel metricCard(String title, JLabel value, String caption) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(13, 15, 13, 15)));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(MUTED);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        JLabel captionLabel = new JLabel(caption);
        captionLabel.setForeground(MUTED);
        captionLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(value);
        card.add(Box.createVerticalStrut(4));
        card.add(captionLabel);
        return card;
    }

    private JPanel createTableArea() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(14, 14, 10, 14)));
        panel.add(createToolbar(), BorderLayout.NORTH);

        JTable table = new JTable(tableModel);
        table.setRowHeight(34);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setGridColor(new Color(226, 232, 240));
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(226, 232, 240));
        table.getTableHeader().setPreferredSize(new Dimension(0, 34));
        table.setAutoCreateRowSorter(false);
        RowSorter<InventoryTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        StockStatusRenderer renderer = new StockStatusRenderer(tableModel);
        for (int column = 0; column < table.getColumnCount(); column++) {
            table.getColumnModel().getColumn(column).setCellRenderer(renderer);
        }
        table.getColumnModel().getColumn(1).setPreferredWidth(240);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(7).setPreferredWidth(100);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setColumnHeaderView(table.getTableHeader());
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setOpaque(false);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.setOpaque(false);
        searchField.setPreferredSize(new Dimension(320, 34));
        searchField.setToolTipText("Search SKU, item, category, or brand");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent event) { applyFilters(); }
            @Override public void removeUpdate(DocumentEvent event) { applyFilters(); }
            @Override public void changedUpdate(DocumentEvent event) { applyFilters(); }
        });
        statusFilter.setPreferredSize(new Dimension(145, 34));
        statusFilter.addActionListener(event -> applyFilters());
        filters.add(new JLabel("Search"));
        filters.add(searchField);
        filters.add(statusFilter);

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(event -> {
            searchField.setText("");
            statusFilter.setSelectedIndex(0);
        });
        filters.add(resetButton);
        toolbar.add(filters, BorderLayout.WEST);

        JButton refreshButton = new JButton("Refresh data");
        refreshButton.setBackground(GREEN);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(event -> refreshInventory());
        toolbar.add(refreshButton, BorderLayout.EAST);
        return toolbar;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        resultsLabel.setForeground(MUTED);
        refreshLabel.setForeground(MUTED);
        refreshLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        footer.add(resultsLabel, BorderLayout.WEST);
        footer.add(refreshLabel, BorderLayout.EAST);
        return footer;
    }

    private void refreshInventory() {
        try {
            applyFilters();
            refreshLabel.setText("Last refreshed " + TIME_FORMAT.format(LocalDateTime.now()));
        } catch (InventoryDataException exception) {
            JOptionPane.showMessageDialog(
                this,
                exception.getMessage(),
                "Inventory data unavailable",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilters() {
        StockStatus status = switch (statusFilter.getSelectedIndex()) {
            case 1 -> StockStatus.IN_STOCK;
            case 2 -> StockStatus.LOW_STOCK;
            case 3 -> StockStatus.OUT_OF_STOCK;
            default -> null;
        };
        List<InventoryItem> visibleItems = service.search(searchField.getText(), status);
        tableModel.setItems(visibleItems);
        updateMetrics(service.summarize(visibleItems));
    }

    private void updateMetrics(InventorySummary summary) {
        skuValue.setText(String.format("%,d", summary.skuCount()));
        unitValue.setText(String.format("%,d", summary.unitCount()));
        lowStockValue.setText(String.format("%,d", summary.lowStockCount() + summary.outOfStockCount()));
        inventoryValue.setText(String.format("TT$ %,.2f", summary.inventoryCost()));
        resultsLabel.setText(summary.skuCount() + " product(s) displayed");
    }

    private static JLabel metricValue() {
        JLabel label = new JLabel("0");
        label.setForeground(NAVY);
        label.setFont(new Font("SansSerif", Font.BOLD, 24));
        return label;
    }
}
