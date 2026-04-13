package com.may.backend.service;

import com.may.backend.dto.request.RolePermissionRequest;
import com.may.backend.dto.response.RolePermissionResponse;
import com.may.backend.entity.PermissionEntity;
import com.may.backend.entity.RoleEntity;
import com.may.backend.entity.RolePermissionEntity;
import com.may.backend.exception.BusinessException;
import com.may.backend.exception.ErrorCode;
import com.may.backend.mapper.RolePermissionMapper;
import com.may.backend.repository.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;
    private final RoleService roleService;
    private final PermissionService permissionService;
    private final RolePermissionMapper rolePermissionMapper;

    @Transactional(readOnly = true)
    public List<RolePermissionResponse> getByRoleId(Long roleId) {
        log.info("Rol izinleri getiriliyor. roleId: {}", roleId);
        return rolePermissionRepository.findAllByRole_Id(roleId).stream()
                .map(rolePermissionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RolePermissionResponse> getByPermissionId(Long permissionId) {
        log.info("İzne atanmış roller getiriliyor. permissionId: {}", permissionId);
        return rolePermissionRepository.findAllByPermission_Id(permissionId).stream()
                .map(rolePermissionMapper::toResponse)
                .toList();
    }

    @Transactional
    public RolePermissionResponse assign(RolePermissionRequest request, String createdIp) {
        log.info("Role izin atanıyor. roleId: {}, permissionId: {}", request.getRoleId(), request.getPermissionId());

        if (rolePermissionRepository.existsByRole_IdAndPermission_Id(request.getRoleId(), request.getPermissionId())) {
            throw new BusinessException("Bu role bu izin zaten atanmış", ErrorCode.DUPLICATE_ENTRY, HttpStatus.CONFLICT);
        }

        RoleEntity role = roleService.findById(request.getRoleId());
        PermissionEntity permission = permissionService.findById(request.getPermissionId());

        RolePermissionEntity entity = new RolePermissionEntity();
        entity.setRole(role);
        entity.setPermission(permission);
        entity.setCreatedBy("SYSTEM");
        entity.setCreatedDate(LocalDateTime.now());
        entity.setCreatedIp(createdIp);

        RolePermissionEntity saved = rolePermissionRepository.save(entity);
        log.info("Role izin atandı. id: {}", saved.getId());
        return rolePermissionMapper.toResponse(saved);
    }

    @Transactional
    public void remove(Long id) {
        log.info("Rol izni kaldırılıyor. id: {}", id);
        RolePermissionEntity entity = rolePermissionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("RolePermission with id " + id + " not found", ErrorCode.ENTITY_NOT_FOUND, HttpStatus.NOT_FOUND));
        rolePermissionRepository.delete(entity);
        log.info("Rol izni kaldırıldı. id: {}", id);
    }
}
