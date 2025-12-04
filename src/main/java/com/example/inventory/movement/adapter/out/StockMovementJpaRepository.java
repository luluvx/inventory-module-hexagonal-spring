package com.example.inventory.movement.adapter.out;

import com.example.inventory.movement.domain.MovementType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface StockMovementJpaRepository extends JpaRepository<StockMovementEntity, UUID> {
    List<StockMovementEntity> findByProductId(UUID productId);
    List<StockMovementEntity> findByBatchId(UUID batchId);
    List<StockMovementEntity> findBySourceBranchId(UUID branchId);
    List<StockMovementEntity> findByDestinationBranchId(UUID branchId);
    List<StockMovementEntity> findByMovementType(MovementType type);
    List<StockMovementEntity> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
    List<StockMovementEntity> findBySourceBranchIdOrDestinationBranchIdOrderByCreatedAtDesc(UUID sourceBranchId, UUID destinationBranchId);
    List<StockMovementEntity> findBySourceBranchIdAndCreatedAtBetweenOrderByCreatedAtDesc(UUID branchId, LocalDateTime from, LocalDateTime to);
    List<StockMovementEntity> findByDestinationBranchIdAndCreatedAtBetweenOrderByCreatedAtDesc(UUID branchId, LocalDateTime from, LocalDateTime to);
    List<StockMovementEntity> findBySourceBranchIdAndMovementTypeAndCreatedAtBetweenOrderByCreatedAtDesc(UUID branchId, MovementType type, LocalDateTime from, LocalDateTime to);
    List<StockMovementEntity> findByDestinationBranchIdAndMovementTypeAndCreatedAtBetweenOrderByCreatedAtDesc(UUID branchId, MovementType type, LocalDateTime from, LocalDateTime to);
    List<StockMovementEntity> findByProductIdAndCreatedAtBetweenOrderByCreatedAtDesc(UUID productId, LocalDateTime from, LocalDateTime to);
}
