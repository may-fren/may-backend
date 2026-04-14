package com.may.backend.entity;

import com.may.backend.entity.base.BaseEntity;
import com.may.backend.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ANALYSIS", schema = "MAY")
@Getter
@Setter
public class AnalysisEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "analysis_seq")
    @SequenceGenerator(name = "analysis_seq", sequenceName = "MAY.SEQ_ANALYSIS", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = false, length = 200)
    private String name;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MODULE_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_ANALYSIS_MODULE"))
    private ModuleEntity module;

    @Column(name = "NOTE", length = 1000)
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private Status status = Status.ACTIVE;
}
