package com.factoryops.quality.entity;

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
@Table(name = "quality_inspections")
public class QualityInspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "inspector_name", nullable = false, length = 100)
    private String inspectorName;

    @Column(name = "inspection_date", nullable = true)
    private LocalDateTime inspectionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false)
    private InspectionResult result;

    @Column(name = "remarks", nullable = true)
    private String remarks;

    @Column(name = "production_batch_id", nullable = false)
    private Long productionBatchId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
