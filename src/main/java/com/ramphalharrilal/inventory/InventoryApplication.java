package com.ramphalharrilal.inventory;

import com.ramphalharrilal.inventory.repository.CsvInventoryRepository;
import com.ramphalharrilal.inventory.repository.InventoryRepository;
import com.ramphalharrilal.inventory.service.InventoryService;
import com.ramphalharrilal.inventory.ui.InventoryFrame;
import java.nio.file.Path;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class InventoryApplication {
    private InventoryApplication() {
    }

    public static void main(String[] args) {
        InventoryRepository repository = args.length > 0
            ? CsvInventoryRepository.fromPath(Path.of(args[0]))
            : CsvInventoryRepository.fromResource("/data/sample-inventory.csv");

        SwingUtilities.invokeLater(() -> {
            setSystemLookAndFeel();
            new InventoryFrame(new InventoryService(repository)).setVisible(true);
        });
    }

    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ignored) {
            // Swing safely uses its cross-platform look and feel.
        }
    }
}
