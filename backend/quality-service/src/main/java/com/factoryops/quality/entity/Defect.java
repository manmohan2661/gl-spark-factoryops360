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
@Table(name = "defects")
public class Defect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "defect_type", nullable = false, length = 100)
    private String defectType;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private DefectSeverity severity;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "reported_date", nullable = true)
    private LocalDateTime reportedDate;

    @Column(name = "resolved", nullable = true)
    private Boolean resolved;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quality_inspection_id", nullable = false)
    private QualityInspection qualityInspection;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
