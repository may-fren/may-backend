package com.may.backend.service;

import com.may.backend.dto.request.RoleCreateRequest;
import com.may.backend.dto.request.RoleUpdateRequest;
import com.may.backend.dto.response.RoleResponse;
import com.may.backend.entity.RoleEntity;
import com.may.backend.exception.BusinessException;
import com.may.backend.exception.ErrorCode;
import com.may.backend.mapper.RoleMapper;
import com.may.backend.repository.RoleRepository;
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
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Transactional(readOnly = true)
    public RoleResponse getById(Long id) {
        log.info("Rol getiriliyor. id: {}", id);
        return roleMapper.toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public Page<RoleResponse> getAll(Map<String, String> filters, Pageable pageable) {
        log.info("Roller listeleniyor. filters: {}", filters);
        Specification<RoleEntity> spec = GenericSpecification.build(RoleEntity.class, filters);
        return roleRepository.findAll(spec, pageable).map(roleMapper::toResponse);
    }

    @Transactional
    public RoleResponse create(RoleCreateRequest request, String createdIp) {
        log.info("Rol oluşturuluyor. name: {}", request.getName());

        if (roleRepository.existsByName(request.getName())) {
            throw new BusinessException("Bu rol adı zaten kullanılıyor", ErrorCode.DUPLICATE_ENTRY, HttpStatus.CONFLICT);
        }

        RoleEntity entity = roleMapper.toEntity(request);
        entity.setCreatedBy("SYSTEM");
        entity.setCreatedDate(LocalDateTime.now());
        entity.setCreatedIp(createdIp);

        RoleEntity saved = roleRepository.save(entity);
        log.info("Rol oluşturuldu. id: {}", saved.getId());
        return roleMapper.toResponse(saved);
    }

    @Transactional
    public RoleResponse update(Long id, RoleUpdateRequest request, String updatedIp) {
        log.info("Rol güncelleniyor. id: {}", id);
        RoleEntity entity = findById(id);

        if (!entity.getName().equals(request.getName()) && roleRepository.existsByName(request.getName())) {
            throw new BusinessException("Bu rol adı zaten kullanılıyor", ErrorCode.DUPLICATE_ENTRY, HttpStatus.CONFLICT);
        }

        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setUpdatedBy("SYSTEM");
        entity.setUpdatedDate(LocalDateTime.now());
        entity.setUpdatedIp(updatedIp);

        log.info("Rol güncellendi. id: {}", id);
        return roleMapper.toResponse(roleRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        log.info("Rol siliniyor. id: {}", id);
        RoleEntity entity = findById(id);
        roleRepository.delete(entity);
        log.info("Rol silindi. id: {}", id);
    }

    public RoleEntity findById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Role with id " + id + " not found", ErrorCode.ENTITY_NOT_FOUND, HttpStatus.NOT_FOUND));
    }
}
