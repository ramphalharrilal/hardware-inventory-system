package com.ramphalharrilal.inventory.ui;

import com.ramphalharrilal.inventory.service.InventoryService;
import java.awt.Dimension;
import javax.swing.JFrame;

public final class InventoryFrame extends JFrame {
    public InventoryFrame(InventoryService service) {
        super("Retail Inventory Operations Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1120, 700));
        setSize(1280, 780);
        setLocationRelativeTo(null);
        setContentPane(new InventoryPanel(service));
    }
}
