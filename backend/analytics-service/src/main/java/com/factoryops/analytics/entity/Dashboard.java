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


    @Column(name = "total_orders")
    private Integer totalOrders;


    @Column(name = "completed_orders")
    private Integer completedOrders;


    @Column(name = "pending_orders")
    private Integer pendingOrders;


    @Column(name = "total_production_quantity")
    private Integer totalProductionQuantity;


    @Column(name = "total_machines")
    private Integer totalMachines;


    @Column(name = "operational_machines")
    private Integer operationalMachines;


    @Column(name = "maintenance_pending")
    private Integer maintenancePending;


    @Column(name = "quality_pass_rate")
    private Double qualityPassRate;


    @Column(name = "active_alerts")
    private Integer activeAlerts;


    @Column(name = "generated_at")
    private LocalDateTime generatedAt;


    @Column(name = "created_at")
    private LocalDateTime createdAt;


    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}