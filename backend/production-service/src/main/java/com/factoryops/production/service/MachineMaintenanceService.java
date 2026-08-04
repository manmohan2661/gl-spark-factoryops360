package com.factoryops.production.service;

import com.factoryops.production.dto.request.MachineMaintenanceRequest;
import com.factoryops.production.dto.response.MachineMaintenanceResponse;

import java.util.List;

public interface MachineMaintenanceService {

    MachineMaintenanceResponse create(MachineMaintenanceRequest request);

    MachineMaintenanceResponse getById(Long id);

    List<MachineMaintenanceResponse> getAll();

    MachineMaintenanceResponse update(Long id, MachineMaintenanceRequest request);

    void delete(Long id);
}
