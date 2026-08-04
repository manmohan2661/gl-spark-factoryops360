package com.factoryops.production.service;

import com.factoryops.production.dto.request.MachineRequest;
import com.factoryops.production.dto.response.MachineResponse;

import java.util.List;

public interface MachineService {

    MachineResponse create(MachineRequest request);

    MachineResponse getById(Long id);

    List<MachineResponse> getAll();

    MachineResponse update(Long id, MachineRequest request);

    void delete(Long id);
}
