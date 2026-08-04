package com.factoryops.quality.service;

import com.factoryops.quality.dto.request.QualityInspectionRequest;
import com.factoryops.quality.dto.response.QualityInspectionResponse;

import java.util.List;

public interface QualityInspectionService {

    QualityInspectionResponse create(QualityInspectionRequest request);

    QualityInspectionResponse getById(Long id);

    List<QualityInspectionResponse> getAll();

    QualityInspectionResponse update(Long id, QualityInspectionRequest request);

    void delete(Long id);
}
