package com.may.backend.repository;

import com.may.backend.entity.RolePermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermissionEntity, Long>, JpaSpecificationExecutor<RolePermissionEntity> {

    List<RolePermissionEntity> findAllByRole_Id(Long roleId);

    List<RolePermissionEntity> findAllByPermission_Id(Long permissionId);

    boolean existsByRole_IdAndPermission_Id(Long roleId, Long permissionId);
}
