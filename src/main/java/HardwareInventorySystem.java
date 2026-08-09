import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

public class HardwareInventorySystem {
    private static final Color NAVY = new Color(18, 45, 76);
    private static final Color GREEN = new Color(38, 128, 76);
    private static final Color LIGHT_GREEN = new Color(231, 245, 236);
    private static final Color LOW_STOCK = new Color(255, 242, 204);
    private static final Color OUT_OF_STOCK = new Color(255, 218, 218);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            setSystemLookAndFeel();
            new InventoryFrame(new DemoInventoryRepository()).setVisible(true);
        });
    }

    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Swing falls back to its default appearance.
        }
    }

    interface InventoryRepository {
        List<InventoryItem> findAll();
    }

    static final class DemoInventoryRepository implements InventoryRepository {
        @Override
        public List<InventoryItem> findAll() {
            return List.of(
                new InventoryItem("HW-1001", "Cordless Drill 20V", "Power Tools", "BuildPro", 425.00, 599.00, 12, 4),
                new InventoryItem("HW-1002", "Claw Hammer 16 oz", "Hand Tools", "ForgeLine", 42.50, 65.00, 3, 5),
                new InventoryItem("HW-1003", "PVC Pipe 1 inch", "Plumbing", "FlowRight", 21.00, 32.00, 28, 10),
                new InventoryItem("HW-1004", "Exterior Paint 1 gallon", "Paint", "ColorShield", 165.00, 229.00, 0, 3),
                new InventoryItem("HW-1005", "LED Bulb 12W", "Electrical", "BrightHome", 18.75, 29.00, 40, 12),
                new InventoryItem("HW-1006", "Adjustable Wrench 10 inch", "Hand Tools", "ForgeLine", 58.00, 85.00, 7, 4),
                new InventoryItem("HW-1007", "Wood Screws 2 inch 100 pack", "Fasteners", "FixMaster", 34.00, 49.00, 15, 6),
                new InventoryItem("HW-1008", "Garden Hose 50 ft", "Garden", "GreenWay", 110.00, 159.00, 2, 4)
            );
        }
    }

    static final class InventoryItem {
        final String sku;
        final String name;
        final String category;
        final String brand;
        final double costPrice;
        final double sellingPrice;
        final int quantity;
        final int reorderLevel;

        InventoryItem(String sku, String name, String category, String brand,
                      double costPrice, double sellingPrice, int quantity, int reorderLevel) {
            this.sku = sku;
            this.name = name;
            this.category = category;
            this.brand = brand;
            this.costPrice = costPrice;
            this.sellingPrice = sellingPrice;
            this.quantity = quantity;
            this.reorderLevel = reorderLevel;
        }

        String status() {
            if (quantity == 0) return "Out of Stock";
            if (quantity <= reorderLevel) return "Low Stock";
            return "In Stock";
        }

        boolean matches(String search) {
            String value = search.toLowerCase(Locale.ROOT);
            return sku.toLowerCase(Locale.ROOT).contains(value)
                || name.toLowerCase(Locale.ROOT).contains(value)
                || category.toLowerCase(Locale.ROOT).contains(value)
                || brand.toLowerCase(Locale.ROOT).contains(value);
        }
    }

    static final class InventoryFrame extends JFrame {
        InventoryFrame(InventoryRepository repository) {
            super("Hardware Inventory System");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setMinimumSize(new Dimension(1050, 650));
            setLocationRelativeTo(null);
            setContentPane(new InventoryPanel(repository));
        }
    }

    static final class InventoryPanel extends JPanel {
        private final InventoryRepository repository;
        private final InventoryTableModel tableModel = new InventoryTableModel();
        private final JTextField searchField = new JTextField();
        private final JLabel resultsLabel = new JLabel();

        InventoryPanel(InventoryRepository repository) {
            this.repository = repository;
            setLayout(new BorderLayout(0, 16));
            setBorder(BorderFactory.createEmptyBorder(22, 24, 14, 24));
            setBackground(Color.WHITE);

            add(createHeader(), BorderLayout.NORTH);
            add(createInventoryArea(), BorderLayout.CENTER);
            add(createFooter(), BorderLayout.SOUTH);
            refreshInventory();
        }

        private JPanel createHeader() {
            JPanel wrapper = new JPanel(new BorderLayout(0, 16));
            wrapper.setOpaque(false);

            JPanel titleBar = new JPanel(new BorderLayout());
            titleBar.setBackground(NAVY);
            titleBar.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

            JLabel title = new JLabel("Hardware Inventory System");
            title.setForeground(Color.WHITE);
            title.setFont(new Font("SansSerif", Font.BOLD, 25));
            titleBar.add(title, BorderLayout.WEST);

            JLabel mode = new JLabel("PORTFOLIO DEMO");
            mode.setOpaque(true);
            mode.setBackground(GREEN);
            mode.setForeground(Color.WHITE);
            mode.setFont(new Font("SansSerif", Font.BOLD, 12));
            mode.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            titleBar.add(mode, BorderLayout.EAST);

            JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
            searchPanel.setOpaque(false);
            searchPanel.add(new JLabel("Search inventory:"), BorderLayout.WEST);
            searchField.setFont(new Font("SansSerif", Font.PLAIN, 16));
            searchField.setToolTipText("Search by SKU, item, category, or brand");
            searchField.addActionListener(event -> applySearch());
            searchPanel.add(searchField, BorderLayout.CENTER);

            JButton searchButton = new JButton("Search");
            searchButton.addActionListener(event -> applySearch());
            searchPanel.add(searchButton, BorderLayout.EAST);

            JButton clearButton = new JButton("Clear");
            clearButton.addActionListener(event -> {
                searchField.setText("");
                applySearch();
            });
            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            buttons.setOpaque(false);
            buttons.add(clearButton);
            searchPanel.add(buttons, BorderLayout.SOUTH);

            wrapper.add(titleBar, BorderLayout.NORTH);
            wrapper.add(searchPanel, BorderLayout.CENTER);
            return wrapper;
        }

        private JPanel createInventoryArea() {
            JPanel panel = new JPanel(new BorderLayout(0, 8));
            panel.setOpaque(false);

            JTable table = new JTable(tableModel);
            table.setRowHeight(32);
            table.setFont(new Font("SansSerif", Font.PLAIN, 13));
            table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
            table.getTableHeader().setBackground(LIGHT_GREEN);
            table.setFillsViewportHeight(true);
            table.setDefaultRenderer(Object.class, new InventoryCellRenderer(tableModel));

            panel.add(resultsLabel, BorderLayout.NORTH);
            panel.add(new JScrollPane(table), BorderLayout.CENTER);
            return panel;
        }

        private JPanel createFooter() {
            JPanel footer = new JPanel(new GridLayout(1, 2));
            footer.setOpaque(false);
            JLabel legend = new JLabel("Yellow: Low Stock     Red: Out of Stock");
            legend.setForeground(new Color(75, 75, 75));
            footer.add(legend);

            JLabel credit = new JLabel("Built by Ramphal Harrilal", JLabel.RIGHT);
            credit.setFont(new Font("SansSerif", Font.BOLD, 12));
            credit.setForeground(NAVY);
            footer.add(credit);
            return footer;
        }

        private void refreshInventory() {
            tableModel.setItems(repository.findAll());
            updateResultsLabel();
        }

        private void applySearch() {
            String query = searchField.getText().trim();
            if (query.isEmpty()) {
                refreshInventory();
                return;
            }

            List<InventoryItem> matches = new ArrayList<>();
            for (InventoryItem item : repository.findAll()) {
                if (item.matches(query)) matches.add(item);
            }
            tableModel.setItems(matches);
            updateResultsLabel();
        }

        private void updateResultsLabel() {
            resultsLabel.setText(tableModel.getRowCount() + " item(s) displayed");
        }
    }

    static final class InventoryTableModel extends AbstractTableModel {
        private final String[] columns = {
            "SKU", "Item", "Category", "Brand", "Cost", "Selling Price", "Stock", "Status"
        };
        private List<InventoryItem> items = new ArrayList<>();

        void setItems(List<InventoryItem> items) {
            this.items = new ArrayList<>(items);
            fireTableDataChanged();
        }

        InventoryItem getItem(int row) {
            return items.get(row);
        }

        @Override public int getRowCount() { return items.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int column) { return columns[column]; }

        @Override
        public Object getValueAt(int row, int column) {
            InventoryItem item = items.get(row);
            return switch (column) {
                case 0 -> item.sku;
                case 1 -> item.name;
                case 2 -> item.category;
                case 3 -> item.brand;
                case 4 -> String.format("$%,.2f", item.costPrice);
                case 5 -> String.format("$%,.2f", item.sellingPrice);
                case 6 -> item.quantity;
                case 7 -> item.status();
                default -> "";
            };
        }
    }

    static final class InventoryCellRenderer extends DefaultTableCellRenderer {
        private final InventoryTableModel model;

        InventoryCellRenderer(InventoryTableModel model) {
            this.model = model;
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(
                JTable table, Object value, boolean selected, boolean focused, int row, int column) {
            java.awt.Component component = super.getTableCellRendererComponent(
                table, value, selected, focused, row, column);

            if (!selected) {
                InventoryItem item = model.getItem(table.convertRowIndexToModel(row));
                component.setBackground(item.quantity == 0 ? OUT_OF_STOCK
                    : item.quantity <= item.reorderLevel ? LOW_STOCK : Color.WHITE);
            }
            return component;
        }
    }
}
