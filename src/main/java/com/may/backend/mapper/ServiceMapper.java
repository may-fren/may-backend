package com.may.backend.mapper;

import com.may.backend.dto.request.ServiceCreateRequest;
import com.may.backend.dto.response.ServiceResponse;
import com.may.backend.entity.ServiceEntity;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {

    public ServiceResponse toResponse(ServiceEntity entity) {
        return ServiceResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .city(entity.getCity())
                .town(entity.getTown())
                .address(entity.getAddress())
                .status(entity.getStatus())
                .createdDate(entity.getCreatedDate())
                .createdBy(entity.getCreatedBy())
                .build();
    }

    public ServiceEntity toEntity(ServiceCreateRequest request) {
        ServiceEntity entity = new ServiceEntity();
        entity.setName(request.getName());
        entity.setCity(request.getCity());
        entity.setTown(request.getTown());
        entity.setAddress(request.getAddress());
        return entity;
    }
}
