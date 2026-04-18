package com.may.backend.service;

import com.may.backend.dto.request.AnalysisCreateRequest;
import com.may.backend.dto.request.AnalysisUpdateRequest;
import com.may.backend.dto.response.AnalysisResponse;
import com.may.backend.entity.AnalysisEntity;
import com.may.backend.entity.ModuleEntity;
import com.may.backend.exception.BusinessException;
import com.may.backend.exception.ErrorCode;
import com.may.backend.mapper.AnalysisMapper;
import com.may.backend.repository.AnalysisRepository;
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
public class AnalysisService {

    private final AnalysisRepository analysisRepository;
    private final AnalysisMapper analysisMapper;
    private final ModuleService moduleService;

    @Transactional(readOnly = true)
    public AnalysisResponse getById(Long id) {
        log.info("Analiz getiriliyor. id: {}", id);
        return analysisMapper.toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public Page<AnalysisResponse> getAll(Map<String, String> filters, Pageable pageable) {
        log.info("Analizler listeleniyor. filters: {}", filters);
        Specification<AnalysisEntity> spec = GenericSpecification.build(AnalysisEntity.class, filters);
        return analysisRepository.findAll(spec, pageable).map(analysisMapper::toResponse);
    }

    @Transactional
    public AnalysisResponse create(AnalysisCreateRequest request, String createdIp) {
        log.info("Analiz oluşturuluyor. name: {}", request.getName());

        if (analysisRepository.existsByName(request.getName())) {
            throw new BusinessException("Bu analiz adı zaten kullanılıyor", ErrorCode.DUPLICATE_ENTRY, HttpStatus.CONFLICT);
        }

        ModuleEntity module = moduleService.findById(request.getModuleId());

        AnalysisEntity entity = analysisMapper.toEntity(request);
        entity.setModule(module);
        entity.setCreatedBy("SYSTEM");
        entity.setCreatedDate(LocalDateTime.now());
        entity.setCreatedIp(createdIp);

        AnalysisEntity saved = analysisRepository.save(entity);
        log.info("Analiz oluşturuldu. id: {}", saved.getId());
        return analysisMapper.toResponse(saved);
    }

    @Transactional
    public AnalysisResponse update(Long id, AnalysisUpdateRequest request, String updatedIp) {
        log.info("Analiz güncelleniyor. id: {}", id);
        AnalysisEntity entity = findById(id);

        if (!entity.getName().equals(request.getName()) && analysisRepository.existsByName(request.getName())) {
            throw new BusinessException("Bu analiz adı zaten kullanılıyor", ErrorCode.DUPLICATE_ENTRY, HttpStatus.CONFLICT);
        }

        ModuleEntity module = moduleService.findById(request.getModuleId());

        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setModule(module);
        entity.setNote(request.getNote());
        entity.setStatus(request.getStatus());
        entity.setUpdatedBy("SYSTEM");
        entity.setUpdatedDate(LocalDateTime.now());
        entity.setUpdatedIp(updatedIp);

        log.info("Analiz güncellendi. id: {}", id);
        return analysisMapper.toResponse(analysisRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        log.info("Analiz siliniyor. id: {}", id);
        AnalysisEntity entity = findById(id);
        analysisRepository.delete(entity);
        log.info("Analiz silindi. id: {}", id);
    }

    public AnalysisEntity findById(Long id) {
        return analysisRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Analysis with id " + id + " not found", ErrorCode.ENTITY_NOT_FOUND, HttpStatus.NOT_FOUND));
    }
}
