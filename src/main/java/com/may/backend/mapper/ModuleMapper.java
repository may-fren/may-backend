package com.may.backend.mapper;

import com.may.backend.dto.request.ModuleCreateRequest;
import com.may.backend.dto.response.ModuleResponse;
import com.may.backend.entity.ModuleEntity;
import org.springframework.stereotype.Component;

@Component
public class ModuleMapper {

    public ModuleResponse toResponse(ModuleEntity entity) {
        return ModuleResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .brand(entity.getBrand())
                .model(entity.getModel())
                .year(entity.getYear())
                .code(entity.getCode())
                .status(entity.getStatus())
                .createdDate(entity.getCreatedDate())
                .createdBy(entity.getCreatedBy())
                .build();
    }

    public ModuleEntity toEntity(ModuleCreateRequest request) {
        ModuleEntity entity = new ModuleEntity();
        entity.setName(request.getName());
        entity.setBrand(request.getBrand());
        entity.setModel(request.getModel());
        entity.setYear(request.getYear());
        entity.setCode(request.getCode());
        return entity;
    }
}
