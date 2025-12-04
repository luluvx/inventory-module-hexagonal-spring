package com.example.inventory.product.adapter.out;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {
    Optional<ProductEntity> findBySku(String sku);
    List<ProductEntity> findByBrand(String brand);
    List<ProductEntity> findByCategory(String category);
    boolean existsBySku(String sku);
    boolean existsBySkuAndIdNot(String sku, UUID excludeId);
    List<ProductEntity> findByNameContainingIgnoreCaseOrBrandContainingIgnoreCaseOrSkuContainingIgnoreCaseOrCategoryContainingIgnoreCase(
            String name, String brand, String sku, String category);
}
