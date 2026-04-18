package com.may.backend.service;

import com.may.backend.dto.request.PermissionCreateRequest;
import com.may.backend.dto.request.PermissionUpdateRequest;
import com.may.backend.dto.response.PermissionResponse;
import com.may.backend.entity.PermissionEntity;
import com.may.backend.exception.BusinessException;
import com.may.backend.exception.ErrorCode;
import com.may.backend.mapper.PermissionMapper;
import com.may.backend.repository.PermissionRepository;
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
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Transactional(readOnly = true)
    public PermissionResponse getById(Long id) {
        log.info("İzin getiriliyor. id: {}", id);
        return permissionMapper.toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public Page<PermissionResponse> getAll(Map<String, String> filters, Pageable pageable) {
        log.info("İzinler listeleniyor. filters: {}", filters);
        Specification<PermissionEntity> spec = GenericSpecification.build(PermissionEntity.class, filters);
        return permissionRepository.findAll(spec, pageable).map(permissionMapper::toResponse);
    }

    @Transactional
    public PermissionResponse create(PermissionCreateRequest request, String createdIp) {
        log.info("İzin oluşturuluyor. name: {}", request.getName());

        if (permissionRepository.existsByName(request.getName())) {
            throw new BusinessException("Bu izin adı zaten kullanılıyor", ErrorCode.DUPLICATE_ENTRY, HttpStatus.CONFLICT);
        }
        if (permissionRepository.existsByModuleAndAction(request.getModule(), request.getAction())) {
            throw new BusinessException("Bu modül ve aksiyon kombinasyonu zaten mevcut", ErrorCode.DUPLICATE_ENTRY, HttpStatus.CONFLICT);
        }

        PermissionEntity entity = permissionMapper.toEntity(request);
        entity.setCreatedBy("SYSTEM");
        entity.setCreatedDate(LocalDateTime.now());
        entity.setCreatedIp(createdIp);

        PermissionEntity saved = permissionRepository.save(entity);
        log.info("İzin oluşturuldu. id: {}", saved.getId());
        return permissionMapper.toResponse(saved);
    }

    @Transactional
    public PermissionResponse update(Long id, PermissionUpdateRequest request, String updatedIp) {
        log.info("İzin güncelleniyor. id: {}", id);
        PermissionEntity entity = findById(id);

        if (!entity.getName().equals(request.getName()) && permissionRepository.existsByName(request.getName())) {
            throw new BusinessException("Bu izin adı zaten kullanılıyor", ErrorCode.DUPLICATE_ENTRY, HttpStatus.CONFLICT);
        }

        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setUpdatedBy("SYSTEM");
        entity.setUpdatedDate(LocalDateTime.now());
        entity.setUpdatedIp(updatedIp);

        log.info("İzin güncellendi. id: {}", id);
        return permissionMapper.toResponse(permissionRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        log.info("İzin siliniyor. id: {}", id);
        PermissionEntity entity = findById(id);
        permissionRepository.delete(entity);
        log.info("İzin silindi. id: {}", id);
    }

    public PermissionEntity findById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Permission with id " + id + " not found", ErrorCode.ENTITY_NOT_FOUND, HttpStatus.NOT_FOUND));
    }
}
