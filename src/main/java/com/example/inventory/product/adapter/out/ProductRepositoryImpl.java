package com.example.inventory.product.adapter.out;

import com.example.inventory.product.application.mapper.ProductMapper;
import com.example.inventory.product.domain.Product;
import com.example.inventory.product.port.out.ProductRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository jpa;
    private final ProductMapper mapper;

    public ProductRepositoryImpl(ProductJpaRepository jpa, ProductMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = mapper.domainToEntity(product);
        ProductEntity saved = jpa.save(entity);
        return mapper.entityToDomain(saved);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpa.findById(id).map(mapper::entityToDomain);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return jpa.findBySku(sku).map(mapper::entityToDomain);
    }

    @Override
    public List<Product> findAll() {
        return jpa.findAll().stream().map(mapper::entityToDomain).toList();
    }

    @Override
    public List<Product> findByBrand(String brand) {
        return jpa.findByBrand(brand).stream().map(mapper::entityToDomain).toList();
    }

    @Override
    public List<Product> findByCategory(String category) {
        return jpa.findByCategory(category).stream().map(mapper::entityToDomain).toList();
    }

    @Override
    public List<Product> search(String keyword) {
        return jpa.findByNameContainingIgnoreCaseOrBrandContainingIgnoreCaseOrSkuContainingIgnoreCaseOrCategoryContainingIgnoreCase(
                keyword, keyword, keyword, keyword
        ).stream().map(mapper::entityToDomain).toList();
    }

    @Override
    public boolean existsBySku(String sku) {
        return jpa.existsBySku(sku);
    }

    @Override
    public boolean existsBySkuAndIdNot(String sku, UUID excludeId) {
        return jpa.existsBySkuAndIdNot(sku, excludeId);
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpa.existsById(id);
    }
}
