package com.may.backend.mapper;

import com.may.backend.dto.request.AnalysisCreateRequest;
import com.may.backend.dto.response.AnalysisResponse;
import com.may.backend.entity.AnalysisEntity;
import org.springframework.stereotype.Component;

@Component
public class AnalysisMapper {

    public AnalysisResponse toResponse(AnalysisEntity entity) {
        return AnalysisResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .moduleId(entity.getModule().getId())
                .moduleName(entity.getModule().getName())
                .note(entity.getNote())
                .status(entity.getStatus())
                .createdDate(entity.getCreatedDate())
                .createdBy(entity.getCreatedBy())
                .build();
    }

    public AnalysisEntity toEntity(AnalysisCreateRequest request) {
        AnalysisEntity entity = new AnalysisEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setNote(request.getNote());
        return entity;
    }
}
