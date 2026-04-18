package com.may.backend.service;

import com.may.backend.dto.request.ServiceCreateRequest;
import com.may.backend.dto.request.ServiceUpdateRequest;
import com.may.backend.dto.response.ServiceResponse;
import com.may.backend.entity.ServiceEntity;
import com.may.backend.exception.BusinessException;
import com.may.backend.exception.ErrorCode;
import com.may.backend.mapper.ServiceMapper;
import com.may.backend.repository.ServiceRepository;
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
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;

    @Transactional(readOnly = true)
    public ServiceResponse getById(Long id) {
        log.info("Servis getiriliyor. id: {}", id);
        return serviceMapper.toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public Page<ServiceResponse> getAll(Map<String, String> filters, Pageable pageable) {
        log.info("Servisler listeleniyor. filters: {}", filters);
        Specification<ServiceEntity> spec = GenericSpecification.build(ServiceEntity.class, filters);
        return serviceRepository.findAll(spec, pageable).map(serviceMapper::toResponse);
    }

    @Transactional
    public ServiceResponse create(ServiceCreateRequest request, String createdIp) {
        log.info("Servis oluşturuluyor. name: {}", request.getName());

        if (serviceRepository.existsByName(request.getName())) {
            throw new BusinessException("Bu servis adı zaten kullanılıyor", ErrorCode.DUPLICATE_ENTRY, HttpStatus.CONFLICT);
        }

        ServiceEntity entity = serviceMapper.toEntity(request);
        entity.setCreatedBy("SYSTEM");
        entity.setCreatedDate(LocalDateTime.now());
        entity.setCreatedIp(createdIp);

        ServiceEntity saved = serviceRepository.save(entity);
        log.info("Servis oluşturuldu. id: {}", saved.getId());
        return serviceMapper.toResponse(saved);
    }

    @Transactional
    public ServiceResponse update(Long id, ServiceUpdateRequest request, String updatedIp) {
        log.info("Servis güncelleniyor. id: {}", id);
        ServiceEntity entity = findById(id);

        if (!entity.getName().equals(request.getName()) && serviceRepository.existsByName(request.getName())) {
            throw new BusinessException("Bu servis adı zaten kullanılıyor", ErrorCode.DUPLICATE_ENTRY, HttpStatus.CONFLICT);
        }

        entity.setName(request.getName());
        entity.setCity(request.getCity());
        entity.setTown(request.getTown());
        entity.setAddress(request.getAddress());
        entity.setStatus(request.getStatus());
        entity.setUpdatedBy("SYSTEM");
        entity.setUpdatedDate(LocalDateTime.now());
        entity.setUpdatedIp(updatedIp);

        log.info("Servis güncellendi. id: {}", id);
        return serviceMapper.toResponse(serviceRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        log.info("Servis siliniyor. id: {}", id);
        ServiceEntity entity = findById(id);
        serviceRepository.delete(entity);
        log.info("Servis silindi. id: {}", id);
    }

    public ServiceEntity findById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Service with id " + id + " not found", ErrorCode.ENTITY_NOT_FOUND, HttpStatus.NOT_FOUND));
    }
}
