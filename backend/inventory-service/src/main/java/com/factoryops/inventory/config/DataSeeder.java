package com.factoryops.inventory.config;

import com.factoryops.inventory.entity.*;
import com.factoryops.inventory.repository.InventoryRepository;
import com.factoryops.inventory.repository.InventoryTransactionRepository;
import com.factoryops.inventory.repository.MaterialRepository;
import com.factoryops.inventory.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final WarehouseRepository warehouseRepository;
    private final MaterialRepository materialRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository transactionRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (warehouseRepository.count() > 0) {
            log.info("Inventory data already seeded. Skipping...");
            return;
        }

        log.info("Starting Inventory Data Seeding...");

        // 1. Create 5 Warehouses
        List<Warehouse> warehouses = new ArrayList<>();
        warehouses.add(createWarehouse("WH-RAW-01", "Raw Material Warehouse", "Zone A", 5000));
        warehouses.add(createWarehouse("WH-RAW-02", "Chemicals Storage", "Zone B", 2000));
        warehouses.add(createWarehouse("WH-PROD-01", "Production Store", "Zone C", 1500));
        warehouses.add(createWarehouse("WH-FG-01", "Finished Goods Warehouse 1", "Zone D", 10000));
        warehouses.add(createWarehouse("WH-FG-02", "Finished Goods Warehouse 2", "Zone E", 8000));
        warehouses = warehouseRepository.saveAll(warehouses);

        // 2. Create 25 Materials
        List<Material> materials = new ArrayList<>();
        String[] categories = {"Raw Material", "Packaging", "Chemical", "Consumable", "Finished Good"};
        for (int i = 1; i <= 25; i++) {
            materials.add(Material.builder()
                    .code(String.format("MAT-%03d", i))
                    .name("Material " + i)
                    .description("Description for Material " + i)
                    .unitOfMeasure(UnitOfMeasure.KG)
                    .category(categories[i % 5])
                    .reorderLevel(100)
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());
        }
        materials = materialRepository.saveAll(materials);

        // 3. Create Inventories and Transactions
        List<Inventory> inventories = new ArrayList<>();
        List<InventoryTransaction> transactions = new ArrayList<>();

        int count = 0;
        for (Material m : materials) {
            Warehouse w = warehouses.get(count % 5);
            
            // Generate some low/critical stock scenarios (e.g., first 5 materials)
            int qtyAvailable = (count < 5) ? 20 : 500 + (count * 10);
            
            Inventory inv = Inventory.builder()
                    .material(m)
                    .warehouse(w)
                    .quantityAvailable(qtyAvailable)
                    .quantityReserved(count % 2 == 0 ? 50 : 0)
                    .lastUpdated(LocalDateTime.now())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            inventories.add(inv);
            count++;
        }
        inventories = inventoryRepository.saveAll(inventories);

        // Transactions
        for (Inventory inv : inventories) {
            transactions.add(InventoryTransaction.builder()
                    .inventory(inv)
                    .transactionType(TransactionType.INBOUND)
                    .quantity(inv.getQuantityAvailable() + inv.getQuantityReserved())
                    .referenceNumber("PO-" + System.currentTimeMillis())
                    .transactionDate(LocalDateTime.now().minusDays(10))
                    .remarks("Initial Stock Receipt")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());
                    
            if (inv.getQuantityReserved() > 0) {
                 transactions.add(InventoryTransaction.builder()
                    .inventory(inv)
                    .transactionType(TransactionType.OUTBOUND)
                    .quantity(inv.getQuantityReserved())
                    .referenceNumber("PROD-" + System.currentTimeMillis())
                    .transactionDate(LocalDateTime.now().minusDays(2))
                    .remarks("Reserved for production")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());
            }
        }
        transactionRepository.saveAll(transactions);

        log.info("Inventory Data Seeding Completed!");
    }

    private Warehouse createWarehouse(String code, String name, String loc, int capacity) {
        return Warehouse.builder()
                .code(code)
                .name(name)
                .location(loc)
                .capacity(capacity)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
