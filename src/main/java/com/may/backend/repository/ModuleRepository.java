package com.may.backend.repository;

import com.may.backend.entity.ModuleEntity;
import com.may.backend.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModuleRepository extends JpaRepository<ModuleEntity, Long> {

    boolean existsByName(String name);

    List<ModuleEntity> findAllByBrand(String brand);

    Page<ModuleEntity> findAllByStatus(Status status, Pageable pageable);
}
