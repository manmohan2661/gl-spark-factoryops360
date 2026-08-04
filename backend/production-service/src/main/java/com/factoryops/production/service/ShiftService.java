package com.factoryops.production.service;

import com.factoryops.production.dto.request.ShiftRequest;
import com.factoryops.production.dto.response.ShiftResponse;

import java.util.List;

public interface ShiftService {

    ShiftResponse create(ShiftRequest request);

    ShiftResponse getById(Long id);

    List<ShiftResponse> getAll();

    ShiftResponse update(Long id, ShiftRequest request);

    void delete(Long id);
}
