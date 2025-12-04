package com.example.inventory.branchstock.application.mapper;

import com.example.inventory.branchstock.adapter.out.BranchStockEntity;
import com.example.inventory.branchstock.domain.BranchStock;
import com.example.inventory.branchstock.dto.BranchStockResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BranchStockMapper {
    BranchStock entityToDomain(BranchStockEntity entity);
    BranchStockEntity domainToEntity(BranchStock domain);
    
    @Mapping(target = "branchName", ignore = true)
    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "productBrand", ignore = true)
    @Mapping(target = "batchNumber", ignore = true)
    @Mapping(target = "lowStock", expression = "java(domain.isLowStock())")
    BranchStockResponseDTO toResponseDTO(BranchStock domain);
    
    List<BranchStock> entitiesToDomains(List<BranchStockEntity> entities);
}
