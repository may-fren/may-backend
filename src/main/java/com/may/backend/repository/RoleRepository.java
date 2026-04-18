package com.may.backend.repository;

import com.may.backend.entity.RoleEntity;
import com.may.backend.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long>, JpaSpecificationExecutor<RoleEntity> {

    Optional<RoleEntity> findByName(String name);

    boolean existsByName(String name);

    Page<RoleEntity> findAllByStatus(Status status, Pageable pageable);
}
