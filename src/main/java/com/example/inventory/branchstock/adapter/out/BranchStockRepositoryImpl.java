package com.example.inventory.branchstock.adapter.out;

import com.example.inventory.branchstock.application.mapper.BranchStockMapper;
import com.example.inventory.branchstock.domain.BranchStock;
import com.example.inventory.branchstock.port.out.BranchStockRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BranchStockRepositoryImpl implements BranchStockRepository {

    private final BranchStockJpaRepository jpa;
    private final BranchStockMapper mapper;

    public BranchStockRepositoryImpl(BranchStockJpaRepository jpa, BranchStockMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public BranchStock save(BranchStock branchStock) {
        BranchStockEntity entity = mapper.domainToEntity(branchStock);
        BranchStockEntity saved = jpa.save(entity);
        return mapper.entityToDomain(saved);
    }

    @Override
    public Optional<BranchStock> findById(UUID id) {
        return jpa.findById(id).map(mapper::entityToDomain);
    }

    @Override
    public List<BranchStock> findByBranchId(UUID branchId) {
        return jpa.findByBranchId(branchId).stream().map(mapper::entityToDomain).toList();
    }

    @Override
    public List<BranchStock> findByProductId(UUID productId) {
        return jpa.findByProductId(productId).stream().map(mapper::entityToDomain).toList();
    }

    @Override
    public List<BranchStock> findByBatchId(UUID batchId) {
        return jpa.findByBatchId(batchId).stream().map(mapper::entityToDomain).toList();
    }

    @Override
    public Optional<BranchStock> findByBranchIdAndBatchId(UUID branchId, UUID batchId) {
        return jpa.findByBranchIdAndBatchId(branchId, batchId).map(mapper::entityToDomain);
    }

    @Override
    public List<BranchStock> findLowStock() {
        return jpa.findAll().stream()
                .filter(bs -> bs.getQuantity() <= bs.getMinimumStock())
                .map(mapper::entityToDomain).toList();
    }

    @Override
    public List<BranchStock> findLowStockByBranch(UUID branchId) {
        return jpa.findByBranchId(branchId).stream()
                .filter(bs -> bs.getQuantity() <= bs.getMinimumStock())
                .map(mapper::entityToDomain).toList();
    }

    @Override
    public int getTotalQuantityByBranchAndProduct(UUID branchId, UUID productId) {
        return jpa.findByBranchIdAndProductId(branchId, productId).stream()
                .mapToInt(BranchStockEntity::getQuantity).sum();
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpa.existsById(id);
    }

    @Override
    public boolean existsByBranchIdAndBatchId(UUID branchId, UUID batchId) {
        return jpa.existsByBranchIdAndBatchId(branchId, batchId);
    }
}
