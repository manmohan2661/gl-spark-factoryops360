package com.factoryops.production.repository;

import com.factoryops.production.entity.MachineMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineMaintenanceRepository extends JpaRepository<MachineMaintenance, Long> {
}
