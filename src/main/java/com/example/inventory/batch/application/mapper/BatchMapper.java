package com.example.inventory.batch.application.mapper;

import com.example.inventory.batch.adapter.out.BatchEntity;
import com.example.inventory.batch.domain.Batch;
import com.example.inventory.batch.dto.BatchResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BatchMapper {
    Batch entityToDomain(BatchEntity entity);
    BatchEntity domainToEntity(Batch domain);
    
    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "productBrand", ignore = true)
    @Mapping(target = "expiringSoon", expression = "java(domain.isExpiringSoon())")
    BatchResponseDTO toResponseDTO(Batch domain);
    
    List<Batch> entitiesToDomains(List<BatchEntity> entities);
}
