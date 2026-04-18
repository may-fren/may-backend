package com.may.backend.service;

import com.may.backend.dto.request.UserRoleRequest;
import com.may.backend.dto.response.UserRoleResponse;
import com.may.backend.entity.RoleEntity;
import com.may.backend.entity.UserEntity;
import com.may.backend.entity.UserRoleEntity;
import com.may.backend.exception.BusinessException;
import com.may.backend.exception.ErrorCode;
import com.may.backend.mapper.UserRoleMapper;
import com.may.backend.repository.UserRepository;
import com.may.backend.repository.UserRoleRepository;
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
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final UserRoleMapper userRoleMapper;

    @Transactional(readOnly = true)
    public Page<UserRoleResponse> getByUserId(Long userId, Map<String, String> filters, Pageable pageable) {
        log.info("Kullanıcı rolleri getiriliyor. userId: {}, filters: {}", userId, filters);
        filters.put("userId", userId.toString());
        Specification<UserRoleEntity> spec = GenericSpecification.build(UserRoleEntity.class, filters);
        return userRoleRepository.findAll(spec, pageable).map(userRoleMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<UserRoleResponse> getByRoleId(Long roleId, Map<String, String> filters, Pageable pageable) {
        log.info("Role atanmış kullanıcılar getiriliyor. roleId: {}, filters: {}", roleId, filters);
        filters.put("roleId", roleId.toString());
        Specification<UserRoleEntity> spec = GenericSpecification.build(UserRoleEntity.class, filters);
        return userRoleRepository.findAll(spec, pageable).map(userRoleMapper::toResponse);
    }

    @Transactional
    public UserRoleResponse assign(UserRoleRequest request, String createdIp) {
        log.info("Kullanıcıya rol atanıyor. userId: {}, roleId: {}", request.getUserId(), request.getRoleId());

        if (userRoleRepository.existsByUser_IdAndRole_Id(request.getUserId(), request.getRoleId())) {
            throw new BusinessException("Bu kullanıcıya bu rol zaten atanmış", ErrorCode.DUPLICATE_ENTRY, HttpStatus.CONFLICT);
        }

        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("User with id " + request.getUserId() + " not found", ErrorCode.ENTITY_NOT_FOUND, HttpStatus.NOT_FOUND));
        RoleEntity role = roleService.findById(request.getRoleId());

        UserRoleEntity entity = new UserRoleEntity();
        entity.setUser(user);
        entity.setRole(role);
        entity.setAssignedBy("SYSTEM");
        entity.setAssignedDate(LocalDateTime.now());
        entity.setCreatedBy("SYSTEM");
        entity.setCreatedDate(LocalDateTime.now());
        entity.setCreatedIp(createdIp);

        UserRoleEntity saved = userRoleRepository.save(entity);
        log.info("Kullanıcıya rol atandı. id: {}", saved.getId());
        return userRoleMapper.toResponse(saved);
    }

    @Transactional
    public void remove(Long id) {
        log.info("Kullanıcı rolü kaldırılıyor. id: {}", id);
        UserRoleEntity entity = userRoleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("UserRole with id " + id + " not found", ErrorCode.ENTITY_NOT_FOUND, HttpStatus.NOT_FOUND));
        userRoleRepository.delete(entity);
        log.info("Kullanıcı rolü kaldırıldı. id: {}", id);
    }
}
