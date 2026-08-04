package com.factoryops.analytics.repository;

import com.factoryops.analytics.entity.Alert;
import com.factoryops.analytics.entity.AlertSeverity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    long countByAcknowledgedTrue();

    long countByAcknowledgedFalse();

    long countBySeverity(AlertSeverity severity);

}