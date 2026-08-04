package com.factoryops.inventory.service.impl;

import com.factoryops.inventory.dto.request.MaterialRequest;
import com.factoryops.inventory.dto.response.MaterialResponse;
import com.factoryops.inventory.entity.Material;
import com.factoryops.inventory.exception.BusinessException;
import com.factoryops.inventory.exception.ResourceNotFoundException;
import com.factoryops.inventory.mapper.MaterialMapper;
import com.factoryops.inventory.repository.MaterialRepository;
import com.factoryops.inventory.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private static final String RESOURCE_NAME = "Material";

    private final MaterialRepository materialRepository;
    private final MaterialMapper materialMapper;

    @Override
    public MaterialResponse create(MaterialRequest request) {

        if (materialRepository.existsByCode(request.getCode())) {
            throw new BusinessException("Material code already exists.");
        }

        Material material = materialMapper.toEntity(request);

        Material savedMaterial = materialRepository.save(material);

        return materialMapper.toResponse(savedMaterial);
    }

    @Override
    public MaterialResponse getById(Long id) {

        Material material = findMaterialOrThrow(id);

        return materialMapper.toResponse(material);
    }

    @Override
    public List<MaterialResponse> getAll() {

        return materialMapper.toResponseList(materialRepository.findAll());
    }

    @Override
    public MaterialResponse update(Long id, MaterialRequest request) {

        Material material = findMaterialOrThrow(id);

        materialRepository.findByCode(request.getCode())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException("Material code already exists.");
                });

        material.setCode(request.getCode());
        material.setName(request.getName());
        material.setDescription(request.getDescription());
        material.setUnitOfMeasure(request.getUnitOfMeasure());
        material.setCategory(request.getCategory());
        material.setReorderLevel(request.getReorderLevel());
        material.setActive(request.getActive());

        Material updatedMaterial = materialRepository.save(material);

        return materialMapper.toResponse(updatedMaterial);
    }

    @Override
    public void delete(Long id) {

        Material material = findMaterialOrThrow(id);

        try {
            materialRepository.delete(material);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException(
                    "Cannot delete material '" + material.getCode()
                            + "' because it is referenced by existing inventory records."
            );
        }
    }

    private Material findMaterialOrThrow(Long id) {

        return materialRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(RESOURCE_NAME, id));
    }
}