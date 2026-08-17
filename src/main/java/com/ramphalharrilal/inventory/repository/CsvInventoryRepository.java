package com.ramphalharrilal.inventory.repository;

import com.ramphalharrilal.inventory.domain.InventoryItem;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class CsvInventoryRepository implements InventoryRepository {
    private static final int COLUMN_COUNT = 8;
    private final ReaderSource readerSource;

    public CsvInventoryRepository(ReaderSource readerSource) {
        this.readerSource = readerSource;
    }

    public static CsvInventoryRepository fromPath(Path path) {
        return new CsvInventoryRepository(() -> Files.newBufferedReader(path, StandardCharsets.UTF_8));
    }

    public static CsvInventoryRepository fromResource(String resourcePath) {
        return new CsvInventoryRepository(() -> {
            InputStream stream = CsvInventoryRepository.class.getResourceAsStream(resourcePath);
            if (stream == null) {
                throw new FileNotFoundException("Classpath resource not found: " + resourcePath);
            }
            return new InputStreamReader(stream, StandardCharsets.UTF_8);
        });
    }

    @Override
    public List<InventoryItem> findAll() {
        try (BufferedReader reader = new BufferedReader(readerSource.open())) {
            String header = reader.readLine();
            if (header == null) {
                throw new InventoryDataException("Inventory CSV is empty");
            }

            List<InventoryItem> items = new ArrayList<>();
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (!line.isBlank()) {
                    items.add(parseItem(line, lineNumber));
                }
            }
            return List.copyOf(items);
        } catch (IOException exception) {
            throw new InventoryDataException("Unable to read inventory data", exception);
        }
    }

    private static InventoryItem parseItem(String line, int lineNumber) {
        List<String> columns = parseCsvLine(line);
        if (columns.size() != COLUMN_COUNT) {
            throw new InventoryDataException(
                "Expected " + COLUMN_COUNT + " columns at CSV line " + lineNumber
                    + " but found " + columns.size());
        }

        try {
            return new InventoryItem(
                columns.get(0),
                columns.get(1),
                columns.get(2),
                columns.get(3),
                new BigDecimal(columns.get(4)),
                new BigDecimal(columns.get(5)),
                Integer.parseInt(columns.get(6)),
                Integer.parseInt(columns.get(7)));
        } catch (IllegalArgumentException exception) {
            throw new InventoryDataException("Invalid inventory data at CSV line " + lineNumber, exception);
        }
    }

    static List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;

        for (int index = 0; index < line.length(); index++) {
            char character = line.charAt(index);
            if (character == '"') {
                if (quoted && index + 1 < line.length() && line.charAt(index + 1) == '"') {
                    current.append('"');
                    index++;
                } else {
                    quoted = !quoted;
                }
            } else if (character == ',' && !quoted) {
                values.add(current.toString().strip());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }

        if (quoted) {
            throw new InventoryDataException("Unclosed quoted value in CSV row");
        }
        values.add(current.toString().strip());
        return values;
    }

    @FunctionalInterface
    public interface ReaderSource {
        Reader open() throws IOException;
    }
}
