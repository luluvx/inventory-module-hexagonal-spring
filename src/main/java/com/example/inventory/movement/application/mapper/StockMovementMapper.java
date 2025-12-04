package com.example.inventory.movement.application.mapper;

import com.example.inventory.movement.adapter.out.StockMovementEntity;
import com.example.inventory.movement.domain.StockMovement;
import com.example.inventory.movement.dto.StockMovementResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StockMovementMapper {
    StockMovement entityToDomain(StockMovementEntity entity);
    StockMovementEntity domainToEntity(StockMovement domain);
    
    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "productBrand", ignore = true)
    @Mapping(target = "batchNumber", ignore = true)
    @Mapping(target = "sourceBranchName", ignore = true)
    @Mapping(target = "destinationBranchName", ignore = true)
    StockMovementResponseDTO toResponseDTO(StockMovement domain);
    
    List<StockMovement> entitiesToDomains(List<StockMovementEntity> entities);
}
