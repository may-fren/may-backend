package com.may.backend.repository;

import com.may.backend.entity.ServiceEntity;
import com.may.backend.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long>, JpaSpecificationExecutor<ServiceEntity> {

    boolean existsByName(String name);

    Page<ServiceEntity> findAllByStatus(Status status, Pageable pageable);
}
