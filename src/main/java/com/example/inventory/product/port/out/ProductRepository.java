package com.example.inventory.product.port.out;

import com.example.inventory.product.domain.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(UUID id);
    Optional<Product> findBySku(String sku);
    List<Product> findAll();
    List<Product> findByBrand(String brand);
    List<Product> findByCategory(String category);
    List<Product> search(String keyword);
    boolean existsBySku(String sku);
    boolean existsBySkuAndIdNot(String sku, UUID excludeId);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
