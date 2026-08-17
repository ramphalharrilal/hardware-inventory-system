package com.ramphalharrilal.inventory.repository;

public final class InventoryDataException extends RuntimeException {
    public InventoryDataException(String message) {
        super(message);
    }

    public InventoryDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
