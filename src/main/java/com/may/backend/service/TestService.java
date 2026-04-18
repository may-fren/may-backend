package com.may.backend.service;

import com.may.backend.dto.request.TestCreateRequest;
import com.may.backend.dto.request.TestUpdateRequest;
import com.may.backend.dto.response.TestResponse;
import com.may.backend.entity.ModuleEntity;
import com.may.backend.entity.TestEntity;
import com.may.backend.exception.BusinessException;
import com.may.backend.exception.ErrorCode;
import com.may.backend.mapper.TestMapper;
import com.may.backend.repository.TestRepository;
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
public class TestService {

    private final TestRepository testRepository;
    private final TestMapper testMapper;
    private final ModuleService moduleService;

    @Transactional(readOnly = true)
    public TestResponse getById(Long id) {
        log.info("Test getiriliyor. id: {}", id);
        return testMapper.toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public Page<TestResponse> getAll(Map<String, String> filters, Pageable pageable) {
        log.info("Testler listeleniyor. filters: {}", filters);
        Specification<TestEntity> spec = GenericSpecification.build(TestEntity.class, filters);
        return testRepository.findAll(spec, pageable).map(testMapper::toResponse);
    }

    @Transactional
    public TestResponse create(TestCreateRequest request, String createdIp) {
        log.info("Test oluşturuluyor. name: {}", request.getName());

        if (testRepository.existsByName(request.getName())) {
            throw new BusinessException("Bu test adı zaten kullanılıyor", ErrorCode.DUPLICATE_ENTRY, HttpStatus.CONFLICT);
        }

        ModuleEntity module = moduleService.findById(request.getModuleId());

        TestEntity entity = testMapper.toEntity(request);
        entity.setModule(module);
        entity.setCreatedBy("SYSTEM");
        entity.setCreatedDate(LocalDateTime.now());
        entity.setCreatedIp(createdIp);

        TestEntity saved = testRepository.save(entity);
        log.info("Test oluşturuldu. id: {}", saved.getId());
        return testMapper.toResponse(saved);
    }

    @Transactional
    public TestResponse update(Long id, TestUpdateRequest request, String updatedIp) {
        log.info("Test güncelleniyor. id: {}", id);
        TestEntity entity = findById(id);

        if (!entity.getName().equals(request.getName()) && testRepository.existsByName(request.getName())) {
            throw new BusinessException("Bu test adı zaten kullanılıyor", ErrorCode.DUPLICATE_ENTRY, HttpStatus.CONFLICT);
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

        log.info("Test güncellendi. id: {}", id);
        return testMapper.toResponse(testRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        log.info("Test siliniyor. id: {}", id);
        TestEntity entity = findById(id);
        testRepository.delete(entity);
        log.info("Test silindi. id: {}", id);
    }

    public TestEntity findById(Long id) {
        return testRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Test with id " + id + " not found", ErrorCode.ENTITY_NOT_FOUND, HttpStatus.NOT_FOUND));
    }
}
