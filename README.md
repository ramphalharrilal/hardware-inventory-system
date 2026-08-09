# Hardware Inventory System

A desktop inventory search application built with Java Swing. It gives staff a fast way to locate hardware items, view prices, check stock levels, and identify low stock products.

This repository is a public portfolio edition. It uses fictional sample inventory and contains no client spreadsheet links, credentials, pricing, or private business information.

## Features

- Search by SKU, product name, category, or brand
- Display cost price, selling price, and quantity in stock
- Highlight low stock and out of stock items
- Filter the inventory as the user types
- Refresh the inventory data
- Clean desktop interface designed for nontechnical users
- Repository interface that allows the data source to be replaced

## Client Edition

The private client edition connects to a live Google Sheet through the Google Sheets API. Inventory changes made in the spreadsheet can be retrieved by the application, while service account credentials remain stored only on the authorized business computer.

That private connection, client branding, and business data are intentionally excluded from this repository.

## Technologies

- Java 17+
- Java Swing
- Object oriented programming
- Repository design pattern
- Table models and filtering

## Run the Project

### Eclipse

1. Create a new Java project.
2. Copy `HardwareInventorySystem.java` from `src/main/java` into the project source folder.
3. Run the file as a Java application.

### Command Line

```bash
javac src/main/java/HardwareInventorySystem.java
java -cp src/main/java HardwareInventorySystem
```

## Sample Data

All included products, quantities, SKUs, and prices are fictional and are provided only to demonstrate the application.

## Future Improvements

- Role based user access
- Barcode scanning
- Sales and purchase recording
- Stock movement history
- Dashboard reports
- Export to CSV and PDF

## Author

Built by **Ramphal Harrilal**

