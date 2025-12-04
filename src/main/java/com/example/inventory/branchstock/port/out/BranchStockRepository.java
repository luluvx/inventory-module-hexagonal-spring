package com.example.inventory.branchstock.port.out;

import com.example.inventory.branchstock.domain.BranchStock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BranchStockRepository {
    BranchStock save(BranchStock branchStock);
    Optional<BranchStock> findById(UUID id);
    List<BranchStock> findAll();
    List<BranchStock> findByBranchId(UUID branchId);
    List<BranchStock> findByProductId(UUID productId);
    List<BranchStock> findByBatchId(UUID batchId);
    Optional<BranchStock> findByBranchIdAndBatchId(UUID branchId, UUID batchId);
    List<BranchStock> findLowStock();
    List<BranchStock> findLowStockByBranch(UUID branchId);
    int getTotalQuantityByBranchAndProduct(UUID branchId, UUID productId);
    int getTotalQuantityByBatch(UUID batchId);
    void deleteById(UUID id);
    boolean existsById(UUID id);
    boolean existsByBranchIdAndBatchId(UUID branchId, UUID batchId);
    boolean existsByBatchId(UUID batchId);
    boolean existsByProductId(UUID productId);
}
