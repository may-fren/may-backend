package com.may.backend.service;

import com.may.backend.dto.request.UserCreateRequest;
import com.may.backend.dto.request.UserUpdateRequest;
import com.may.backend.dto.response.UserResponse;
import com.may.backend.entity.UserEntity;
import com.may.backend.enums.Status;
import com.may.backend.exception.BusinessException;
import com.may.backend.exception.ErrorCode;
import com.may.backend.mapper.UserMapper;
import com.may.backend.repository.UserRepository;
import com.may.backend.specification.GenericSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        log.info("Kullanıcı getiriliyor. id: {}", id);
        return userMapper.toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAll(Map<String, String> filters, Pageable pageable) {
        log.info("Kullanıcılar listeleniyor. filters: {}", filters);
        Specification<UserEntity> spec = GenericSpecification.build(UserEntity.class, filters);
        return userRepository.findAll(spec, pageable).map(userMapper::toResponse);
    }

    @Transactional
    public UserResponse create(UserCreateRequest request, String createdIp) {
        log.info("Kullanıcı oluşturuluyor. username: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Bu kullanıcı adı zaten kullanılıyor", ErrorCode.DUPLICATE_ENTRY, HttpStatus.CONFLICT);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Bu email adresi zaten kullanılıyor", ErrorCode.DUPLICATE_ENTRY, HttpStatus.CONFLICT);
        }

        UserEntity entity = userMapper.toEntity(request, passwordEncoder.encode(request.getPassword()));
        entity.setCreatedBy("SYSTEM");
        entity.setCreatedDate(LocalDateTime.now());
        entity.setCreatedIp(createdIp);

        UserEntity saved = userRepository.save(entity);
        log.info("Kullanıcı oluşturuldu. id: {}", saved.getId());
        return userMapper.toResponse(saved);
    }

    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request, String updatedIp) {
        log.info("Kullanıcı güncelleniyor. id: {}", id);
        UserEntity entity = findById(id);

        if (request.getEmail() != null && !request.getEmail().equals(entity.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Bu email adresi zaten kullanılıyor", ErrorCode.DUPLICATE_ENTRY, HttpStatus.CONFLICT);
        }

        entity.setFirstName(request.getFirstName());
        entity.setLastName(request.getLastName());
        if (request.getEmail() != null) entity.setEmail(request.getEmail());
        if (request.getPhone() != null) entity.setPhone(request.getPhone());
        entity.setUpdatedBy("SYSTEM");
        entity.setUpdatedDate(LocalDateTime.now());
        entity.setUpdatedIp(updatedIp);

        log.info("Kullanıcı güncellendi. id: {}", id);
        return userMapper.toResponse(userRepository.save(entity));
    }

    @Transactional
    public void changeStatus(Long id, Status status, String updatedIp) {
        log.info("Kullanıcı status değiştiriliyor. id: {}, status: {}", id, status);
        if (status == Status.CANCELLED) {
            throw new BusinessException("Kullanıcı için geçersiz status: " + status, ErrorCode.VALIDATION_ERROR, HttpStatus.BAD_REQUEST);
        }
        UserEntity entity = findById(id);
        entity.setStatus(status);
        entity.setUpdatedBy("SYSTEM");
        entity.setUpdatedDate(LocalDateTime.now());
        entity.setUpdatedIp(updatedIp);
        userRepository.save(entity);
        log.info("Kullanıcı status değiştirildi. id: {}", id);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Kullanıcı siliniyor. id: {}", id);
        UserEntity entity = findById(id);
        userRepository.delete(entity);
        log.info("Kullanıcı silindi. id: {}", id);
    }

    public UserEntity findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User with id " + id + " not found", ErrorCode.ENTITY_NOT_FOUND, HttpStatus.NOT_FOUND));
    }
}
