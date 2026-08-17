package com.ramphalharrilal.inventory.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.StringReader;
import org.junit.jupiter.api.Test;

class CsvInventoryRepositoryTest {
    @Test
    void readsQuotedProductNamesAndNumericFields() {
        String csv = """
            sku,name,category,brand,cost_price,selling_price,quantity,reorder_level
            HW-1,"Pliers, 8 inch",Hand Tools,ForgeLine,50.00,75.00,3,4
            """;
        CsvInventoryRepository repository = new CsvInventoryRepository(() -> new StringReader(csv));

        var items = repository.findAll();

        assertEquals(1, items.size());
        assertEquals("Pliers, 8 inch", items.get(0).name());
        assertEquals(3, items.get(0).quantity());
    }

    @Test
    void rejectsRowsWithMissingColumns() {
        String csv = """
            sku,name,category,brand,cost_price,selling_price,quantity,reorder_level
            HW-1,Drill,Power Tools
            """;
        CsvInventoryRepository repository = new CsvInventoryRepository(() -> new StringReader(csv));

        assertThrows(InventoryDataException.class, repository::findAll);
    }
}
