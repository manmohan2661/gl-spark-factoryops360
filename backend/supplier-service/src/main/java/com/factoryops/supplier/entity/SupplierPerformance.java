package com.factoryops.supplier.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "supplier_performances")
public class SupplierPerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "evaluation_period", nullable = false)
    private String evaluationPeriod;

    @Column(name = "on_time_delivery_rate", nullable = true)
    private Double onTimeDeliveryRate;

    @Column(name = "quality_score", nullable = true)
    private Double qualityScore;

    @Column(name = "defect_rate", nullable = true)
    private Double defectRate;

    @Column(name = "remarks", nullable = true)
    private String remarks;

    @Column(name = "evaluated_at", nullable = true)
    private LocalDateTime evaluatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
