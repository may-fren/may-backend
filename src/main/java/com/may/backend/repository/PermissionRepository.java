package com.may.backend.repository;

import com.may.backend.entity.PermissionEntity;
import com.may.backend.enums.PermissionAction;
import com.may.backend.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {

    Optional<PermissionEntity> findByName(String name);

    boolean existsByName(String name);

    boolean existsByModuleAndAction(String module, PermissionAction action);

    List<PermissionEntity> findAllByModule(String module);

    Page<PermissionEntity> findAllByStatus(Status status, Pageable pageable);
}
