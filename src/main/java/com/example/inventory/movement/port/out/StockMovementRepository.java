package com.example.inventory.movement.port.out;

import com.example.inventory.movement.domain.MovementType;
import com.example.inventory.movement.domain.StockMovement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockMovementRepository {
    StockMovement save(StockMovement movement);
    Optional<StockMovement> findById(UUID id);
    List<StockMovement> findAll();
    List<StockMovement> findByProductId(UUID productId);
    List<StockMovement> findByBatchId(UUID batchId);
    List<StockMovement> findBySourceBranchId(UUID branchId);
    List<StockMovement> findByDestinationBranchId(UUID branchId);
    List<StockMovement> findByBranchId(UUID branchId);
    List<StockMovement> findByMovementType(MovementType type);
    List<StockMovement> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
    List<StockMovement> findByBranchIdAndCreatedAtBetween(UUID branchId, LocalDateTime from, LocalDateTime to);
    List<StockMovement> findByBranchIdAndMovementTypeAndCreatedAtBetween(UUID branchId, MovementType type, LocalDateTime from, LocalDateTime to);
    List<StockMovement> findByProductIdAndCreatedAtBetween(UUID productId, LocalDateTime from, LocalDateTime to);
}
