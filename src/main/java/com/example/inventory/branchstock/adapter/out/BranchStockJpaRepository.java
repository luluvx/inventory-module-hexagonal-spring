package com.example.inventory.branchstock.adapter.out;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BranchStockJpaRepository extends JpaRepository<BranchStockEntity, UUID> {
    List<BranchStockEntity> findByBranchId(UUID branchId);
    List<BranchStockEntity> findByProductId(UUID productId);
    List<BranchStockEntity> findByBatchId(UUID batchId);
    Optional<BranchStockEntity> findByBranchIdAndBatchId(UUID branchId, UUID batchId);
    boolean existsByBranchIdAndBatchId(UUID branchId, UUID batchId);
    List<BranchStockEntity> findByQuantityLessThanEqualAndMinimumStockGreaterThan(int quantity, int minStock);
    List<BranchStockEntity> findByBranchIdAndProductId(UUID branchId, UUID productId);
}
