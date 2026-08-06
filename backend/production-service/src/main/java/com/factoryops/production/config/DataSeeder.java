package com.factoryops.production.config;

import com.factoryops.production.entity.*;
import com.factoryops.production.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final MachineRepository machineRepository;
    private final ProductionOrderRepository productionOrderRepository;
    private final MachineMaintenanceRepository maintenanceRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (machineRepository.count() > 0) {
            log.info("Production data already seeded. Skipping...");
            return;
        }

        log.info("Starting Production Data Seeding...");

        // 1. Create 10 Machines
        List<Machine> machines = new ArrayList<>();
        machines.add(createMachine("MCH-001", "CNC Lathe A1", "CNC", MachineStatus.OPERATIONAL));
        machines.add(createMachine("MCH-002", "CNC Lathe A2", "CNC", MachineStatus.OPERATIONAL));
        machines.add(createMachine("MCH-003", "Milling Machine B1", "Milling", MachineStatus.UNDER_MAINTENANCE));
        machines.add(createMachine("MCH-004", "Milling Machine B2", "Milling", MachineStatus.OPERATIONAL));
        machines.add(createMachine("MCH-005", "Robotic Arm X1", "Assembly", MachineStatus.OPERATIONAL));
        machines.add(createMachine("MCH-006", "Robotic Arm X2", "Assembly", MachineStatus.BREAKDOWN));
        machines.add(createMachine("MCH-007", "Injection Molder 1", "Molding", MachineStatus.OPERATIONAL));
        machines.add(createMachine("MCH-008", "Injection Molder 2", "Molding", MachineStatus.OPERATIONAL));
        machines.add(createMachine("MCH-009", "Packaging Line 1", "Packaging", MachineStatus.OPERATIONAL));
        machines.add(createMachine("MCH-010", "Packaging Line 2", "Packaging", MachineStatus.OPERATIONAL));
        machines = machineRepository.saveAll(machines);

        // 2. Create 30 Production Orders
        List<ProductionOrder> orders = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            ProductionOrderStatus status = ProductionOrderStatus.COMPLETED;
            int ordered = 1000 + (i * 100);
            int produced = ordered;

            if (i > 18) status = ProductionOrderStatus.IN_PROGRESS;
            if (i > 26) status = ProductionOrderStatus.PLANNED;
            if (i == 20 || i == 21) status = ProductionOrderStatus.ON_HOLD;

            if (status == ProductionOrderStatus.IN_PROGRESS || status == ProductionOrderStatus.ON_HOLD) {
                produced = ordered / 2;
            } else if (status == ProductionOrderStatus.PLANNED) {
                produced = 0;
            }

            orders.add(ProductionOrder.builder()
                    .orderNumber(String.format("PO-2026-%04d", i))
                    .productName("Finished Product Type " + (i % 5 + 1))
                    .quantityOrdered(ordered)
                    .quantityProduced(produced)
                    .status(status)
                    .priority(i % 3 == 0 ? 1 : 2) // 1=High, 2=Normal
                    .startDate(LocalDate.now().minusDays(30 - i))
                    .endDate(status == ProductionOrderStatus.COMPLETED ? LocalDate.now().minusDays(10 - i) : null)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());
        }
        productionOrderRepository.saveAll(orders);

        // 3. Create Maintenance Records
        List<MachineMaintenance> maintenances = new ArrayList<>();
        for (Machine m : machines) {
            if (m.getStatus() == MachineStatus.UNDER_MAINTENANCE) {
                maintenances.add(createMaintenance(m, MaintenanceType.PREVENTIVE, MaintenanceStatus.IN_PROGRESS));
            } else if (m.getStatus() == MachineStatus.BREAKDOWN) {
                maintenances.add(createMaintenance(m, MaintenanceType.CORRECTIVE, MaintenanceStatus.SCHEDULED));
            } else {
                maintenances.add(createMaintenance(m, MaintenanceType.PREVENTIVE, MaintenanceStatus.COMPLETED));
            }
        }
        maintenanceRepository.saveAll(maintenances);

        log.info("Production Data Seeding Completed!");
    }

    private Machine createMachine(String code, String name, String type, MachineStatus status) {
        return Machine.builder()
                .machineCode(code)
                .name(name)
                .type(type)
                .status(status)
                .location("Shop Floor 1")
                .installationDate(LocalDate.now().minusYears(2))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private MachineMaintenance createMaintenance(Machine m, MaintenanceType type, MaintenanceStatus status) {
        return MachineMaintenance.builder()
                .machine(m)
                .maintenanceType(type)
                .status(status)
                .scheduledDate(LocalDate.now().minusDays(2))
                .completedDate(status == MaintenanceStatus.COMPLETED ? LocalDate.now().minusDays(1) : null)
                .remarks("Routine check and parts replacement")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
