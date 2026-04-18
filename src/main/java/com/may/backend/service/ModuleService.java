package com.may.backend.service;

import com.may.backend.dto.request.ModuleCreateRequest;
import com.may.backend.dto.request.ModuleUpdateRequest;
import com.may.backend.dto.response.ModuleResponse;
import com.may.backend.entity.ModuleEntity;
import com.may.backend.exception.BusinessException;
import com.may.backend.exception.ErrorCode;
import com.may.backend.mapper.ModuleMapper;
import com.may.backend.repository.ModuleRepository;
import com.may.backend.specification.GenericSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final ModuleMapper moduleMapper;

    @Transactional(readOnly = true)
    public ModuleResponse getById(Long id) {
        log.info("Modül getiriliyor. id: {}", id);
        return moduleMapper.toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public Page<ModuleResponse> getAll(Map<String, String> filters, Pageable pageable) {
        log.info("Modüller listeleniyor. filters: {}", filters);
        Specification<ModuleEntity> spec = GenericSpecification.build(ModuleEntity.class, filters);
        return moduleRepository.findAll(spec, pageable).map(moduleMapper::toResponse);
    }

    @Transactional
    public ModuleResponse create(ModuleCreateRequest request, String createdIp) {
        log.info("Modül oluşturuluyor. name: {}", request.getName());

        if (moduleRepository.existsByName(request.getName())) {
            throw new BusinessException("Bu modül adı zaten kullanılıyor", ErrorCode.DUPLICATE_ENTRY, HttpStatus.CONFLICT);
        }

        ModuleEntity entity = moduleMapper.toEntity(request);
        entity.setCreatedBy("SYSTEM");
        entity.setCreatedDate(LocalDateTime.now());
        entity.setCreatedIp(createdIp);

        ModuleEntity saved = moduleRepository.save(entity);
        log.info("Modül oluşturuldu. id: {}", saved.getId());
        return moduleMapper.toResponse(saved);
    }

    @Transactional
    public ModuleResponse update(Long id, ModuleUpdateRequest request, String updatedIp) {
        log.info("Modül güncelleniyor. id: {}", id);
        ModuleEntity entity = findById(id);

        if (!entity.getName().equals(request.getName()) && moduleRepository.existsByName(request.getName())) {
            throw new BusinessException("Bu modül adı zaten kullanılıyor", ErrorCode.DUPLICATE_ENTRY, HttpStatus.CONFLICT);
        }

        entity.setName(request.getName());
        entity.setBrand(request.getBrand());
        entity.setModel(request.getModel());
        entity.setYear(request.getYear());
        entity.setCode(request.getCode());
        entity.setStatus(request.getStatus());
        entity.setUpdatedBy("SYSTEM");
        entity.setUpdatedDate(LocalDateTime.now());
        entity.setUpdatedIp(updatedIp);

        log.info("Modül güncellendi. id: {}", id);
        return moduleMapper.toResponse(moduleRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        log.info("Modül siliniyor. id: {}", id);
        ModuleEntity entity = findById(id);
        moduleRepository.delete(entity);
        log.info("Modül silindi. id: {}", id);
    }

    public ModuleEntity findById(Long id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Module with id " + id + " not found", ErrorCode.ENTITY_NOT_FOUND, HttpStatus.NOT_FOUND));
    }
}
