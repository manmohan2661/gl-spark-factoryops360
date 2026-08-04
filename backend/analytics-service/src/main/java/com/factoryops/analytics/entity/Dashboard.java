package com.factoryops.analytics.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dashboards")
public class Dashboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= SUMMARY =================

    @Column(name = "total_orders", nullable = false)
    private Integer totalOrders;

    @Column(name = "completed_orders", nullable = false)
    private Integer completedOrders;

    @Column(name = "pending_orders", nullable = false)
    private Integer pendingOrders;

    @Column(name = "total_production_quantity", nullable = false)
    private Integer totalProductionQuantity;

    // ================= PRODUCTION =================

    @Column(name = "total_machines", nullable = false)
    private Integer totalMachines;

    @Column(name = "operational_machines", nullable = false)
    private Integer operationalMachines;

    @Column(name = "maintenance_pending", nullable = false)
    private Integer maintenancePending;

    // ================= INVENTORY =================

    @Column(name = "total_materials", nullable = false)
    private Integer totalMaterials;

    @Column(name = "low_stock_materials", nullable = false)
    private Integer lowStockMaterials;

    @Column(name = "out_of_stock_materials", nullable = false)
    private Integer outOfStockMaterials;

    @Column(name = "inventory_value")
    private Double inventoryValue;

    @Column(name = "total_warehouses", nullable = false)
    private Integer totalWarehouses;

    // ================= SUPPLIER =================

    @Column(name = "total_suppliers", nullable = false)
    private Integer totalSuppliers;

    @Column(name = "active_suppliers", nullable = false)
    private Integer activeSuppliers;

    @Column(name = "average_supplier_rating")
    private Double averageSupplierRating;

    @Column(name = "pending_deliveries", nullable = false)
    private Integer pendingDeliveries;

    @Column(name = "completed_deliveries", nullable = false)
    private Integer completedDeliveries;

    // ================= QUALITY =================

    @Column(name = "quality_pass_rate")
    private Double qualityPassRate;

    @Column(name = "total_inspections", nullable = false)
    private Integer totalInspections;

    @Column(name = "passed_inspections", nullable = false)
    private Integer passedInspections;

    @Column(name = "failed_inspections", nullable = false)
    private Integer failedInspections;

    @Column(name = "total_defects", nullable = false)
    private Integer totalDefects;

    // ================= ALERT =================

    @Column(name = "active_alerts", nullable = false)
    private Integer activeAlerts;

    @Column(name = "critical_alerts", nullable = false)
    private Integer criticalAlerts;

    @Column(name = "warning_alerts", nullable = false)
    private Integer warningAlerts;

    @Column(name = "acknowledged_alerts", nullable = false)
    private Integer acknowledgedAlerts;

    @Column(name = "unacknowledged_alerts", nullable = false)
    private Integer unAcknowledgedAlerts;

    // ================= SYSTEM =================

    @Column(name = "overall_health_score")
    private Double overallHealthScore;

    @Column(name = "healthy_services")
    private Integer healthyServices;

    @Column(name = "unhealthy_services")
    private Integer unhealthyServices;

    @Column(name = "total_services")
    private Integer totalServices;

    @Column(name = "response_time")
    private Long responseTime;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}