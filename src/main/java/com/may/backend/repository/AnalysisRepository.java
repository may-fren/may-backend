package com.may.backend.repository;

import com.may.backend.entity.AnalysisEntity;
import com.may.backend.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisRepository extends JpaRepository<AnalysisEntity, Long> {

    boolean existsByName(String name);

    List<AnalysisEntity> findAllByModule_Id(Long moduleId);

    Page<AnalysisEntity> findAllByStatus(Status status, Pageable pageable);
}
