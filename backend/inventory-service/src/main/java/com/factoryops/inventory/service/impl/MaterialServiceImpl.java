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
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private static final String RESOURCE_NAME = "Material";

    private final MaterialRepository materialRepository;
    private final MaterialMapper materialMapper;

    @Transactional
    @Override
    public MaterialResponse create(MaterialRequest request) {

        log.info("Creating material {}", request.getCode());

        if (materialRepository.existsByCode(request.getCode())) {
            throw new BusinessException("Material code already exists.");
        }

        Material material = materialMapper.toEntity(request);

        Material savedMaterial = materialRepository.save(material);

        log.info("Material created successfully : {}", savedMaterial.getId());

        return materialMapper.toResponse(savedMaterial);
    }

    @Transactional(readOnly = true)
    @Override
    public MaterialResponse getById(Long id) {

        log.debug("Fetching material {}", id);

        Material material = findMaterialOrThrow(id);

        return materialMapper.toResponse(material);
    }

    @Transactional(readOnly = true)
    @Override
    public List<MaterialResponse> getAll() {

        log.debug("Fetching all materials");

        return materialMapper.toResponseList(
                materialRepository.findAll()
        );
    }

    @Transactional
    @Override
    public MaterialResponse update(Long id,
                                   MaterialRequest request) {

        log.info("Updating material {}", id);

        Material material = findMaterialOrThrow(id);

        materialRepository.findByCode(request.getCode())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException(
                            "Material code already exists.");
                });

        material.setCode(request.getCode());
        material.setName(request.getName());
        material.setDescription(request.getDescription());
        material.setUnitOfMeasure(request.getUnitOfMeasure());
        material.setCategory(request.getCategory());
        material.setReorderLevel(request.getReorderLevel());
        material.setActive(request.getActive());

        Material updatedMaterial = materialRepository.save(material);

        log.info("Material updated successfully : {}", updatedMaterial.getId());

        return materialMapper.toResponse(updatedMaterial);
    }

    @Transactional
    @Override
    public void delete(Long id) {

        log.info("Deleting material {}", id);

        Material material = findMaterialOrThrow(id);

        try {

            materialRepository.delete(material);

            log.info("Material deleted successfully : {}", id);

        } catch (DataIntegrityViolationException ex) {

            log.error("Failed to delete material {}", id);

            throw new BusinessException(
                    "Cannot delete material '"
                            + material.getCode()
                            + "' because it is referenced by existing inventory records."
            );
        }
    }

    private Material findMaterialOrThrow(Long id) {

        return materialRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                RESOURCE_NAME,
                                id
                        ));
    }
}