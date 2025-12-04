package com.example.inventory.product.application.mapper;

import com.example.inventory.product.adapter.out.ProductEntity;
import com.example.inventory.product.domain.Product;
import com.example.inventory.product.dto.ProductResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product entityToDomain(ProductEntity entity);
    ProductEntity domainToEntity(Product domain);
    ProductResponseDTO toResponseDTO(Product domain);
    List<Product> entitiesToDomains(List<ProductEntity> entities);
}
