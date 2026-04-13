package com.may.backend.entity;

import com.may.backend.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "USERS_ROLE", schema = "MAY")
@Getter
@Setter
public class UserRoleEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_role_seq")
    @SequenceGenerator(name = "users_role_seq", sequenceName = "MAY.SEQ_USERS_ROLE", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERS_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_USERS_ROLE_USERS"))
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_USERS_ROLE_ROLE"))
    private RoleEntity role;

    @Column(name = "ASSIGNED_BY", nullable = false, length = 100)
    private String assignedBy;

    @Column(name = "ASSIGNED_DATE", nullable = false)
    private LocalDateTime assignedDate;
}
