package com.may.backend.repository;

import com.may.backend.entity.TestEntity;
import com.may.backend.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TestRepository extends JpaRepository<TestEntity, Long>, JpaSpecificationExecutor<TestEntity> {

    boolean existsByName(String name);

    List<TestEntity> findAllByModule_Id(Long moduleId);

    Page<TestEntity> findAllByStatus(Status status, Pageable pageable);
}
