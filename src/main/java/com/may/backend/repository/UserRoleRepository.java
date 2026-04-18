package com.may.backend.repository;

import com.may.backend.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long>, JpaSpecificationExecutor<UserRoleEntity> {

    List<UserRoleEntity> findAllByUser_Id(Long userId);

    List<UserRoleEntity> findAllByRole_Id(Long roleId);

    boolean existsByUser_IdAndRole_Id(Long userId, Long roleId);
}
