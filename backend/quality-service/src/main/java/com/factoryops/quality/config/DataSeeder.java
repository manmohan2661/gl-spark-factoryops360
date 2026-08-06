package com.factoryops.quality.config;

import com.factoryops.quality.entity.*;
import com.factoryops.quality.repository.DefectRepository;
import com.factoryops.quality.repository.QualityInspectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final QualityInspectionRepository inspectionRepository;
    private final DefectRepository defectRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (inspectionRepository.count() > 0) {
            log.info("Quality data already seeded. Skipping...");
            return;
        }

        log.info("Starting Quality Data Seeding...");
        Random random = new Random();

        List<QualityInspection> inspections = new ArrayList<>();
        int defectCount = 0;
        List<Defect> defects = new ArrayList<>();

        for (int i = 1; i <= 50; i++) {
            InspectionResult result = InspectionResult.PASS;
            if (i % 10 == 0) result = InspectionResult.FAIL; // 10% failure rate
            else if (i == 49) result = InspectionResult.PENDING;

            QualityInspection inspection = QualityInspection.builder()
                    .inspectorName("Inspector " + (i % 5 + 1))
                    .inspectionDate(result == InspectionResult.PENDING ? null : LocalDateTime.now().minusHours(i * 2L))
                    .result(result)
                    .remarks(result == InspectionResult.PASS ? "All tolerances met" : "Defects found during inspection")
                    .productionBatchId((long) (i % 30 + 1)) // Map back to 30 production orders approximately
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            inspections.add(inspection);
        }
        
        inspections = inspectionRepository.saveAll(inspections);

        for (QualityInspection insp : inspections) {
            if (insp.getResult() == InspectionResult.FAIL) {
                // Generate 2-3 defects per failed inspection to reach ~15 defects total
                for (int d = 0; d < 3; d++) {
                    DefectSeverity severity = DefectSeverity.values()[random.nextInt(DefectSeverity.values().length)];
                    boolean resolved = random.nextBoolean();
                    
                    defects.add(Defect.builder()
                            .defectType("Defect Type " + (defectCount % 5 + 1))
                            .severity(severity)
                            .description("Found anomaly in dimensions or material strength.")
                            .reportedDate(insp.getInspectionDate())
                            .resolved(resolved)
                            .qualityInspection(insp)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build());
                    defectCount++;
                }
            }
        }
        defectRepository.saveAll(defects);

        log.info("Quality Data Seeding Completed!");
    }
}
