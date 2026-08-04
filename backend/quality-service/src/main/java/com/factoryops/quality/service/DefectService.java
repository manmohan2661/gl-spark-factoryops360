package com.factoryops.quality.service;

import com.factoryops.quality.dto.request.DefectRequest;
import com.factoryops.quality.dto.response.DefectResponse;

import java.util.List;

public interface DefectService {

    DefectResponse create(DefectRequest request);

    DefectResponse getById(Long id);

    List<DefectResponse> getAll();

    DefectResponse update(Long id, DefectRequest request);

    void delete(Long id);
}
