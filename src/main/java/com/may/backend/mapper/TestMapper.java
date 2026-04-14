package com.may.backend.mapper;

import com.may.backend.dto.request.TestCreateRequest;
import com.may.backend.dto.response.TestResponse;
import com.may.backend.entity.TestEntity;
import org.springframework.stereotype.Component;

@Component
public class TestMapper {

    public TestResponse toResponse(TestEntity entity) {
        return TestResponse.builder()
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

    public TestEntity toEntity(TestCreateRequest request) {
        TestEntity entity = new TestEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setNote(request.getNote());
        return entity;
    }
}
