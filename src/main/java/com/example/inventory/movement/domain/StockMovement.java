package com.example.inventory.movement.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class StockMovement {
    private UUID id;
    private UUID productId;
    private UUID batchId;
    private UUID sourceBranchId;
    private UUID destinationBranchId;
    private int quantity;
    private MovementType movementType;
    private String reason;
    private String performedBy;
    private LocalDateTime createdAt;

    public StockMovement() {}

    public StockMovement(UUID id, UUID productId, UUID batchId, UUID sourceBranchId,
                         UUID destinationBranchId, int quantity, MovementType movementType,
                         String reason, String performedBy, LocalDateTime createdAt) {
        this.id = id;
        this.productId = productId;
        this.batchId = batchId;
        this.sourceBranchId = sourceBranchId;
        this.destinationBranchId = destinationBranchId;
        this.quantity = quantity;
        this.movementType = movementType;
        this.reason = reason;
        this.performedBy = performedBy;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }

    public UUID getBatchId() { return batchId; }
    public void setBatchId(UUID batchId) { this.batchId = batchId; }

    public UUID getSourceBranchId() { return sourceBranchId; }
    public void setSourceBranchId(UUID sourceBranchId) { this.sourceBranchId = sourceBranchId; }

    public UUID getDestinationBranchId() { return destinationBranchId; }
    public void setDestinationBranchId(UUID destinationBranchId) { this.destinationBranchId = destinationBranchId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public MovementType getMovementType() { return movementType; }
    public void setMovementType(MovementType movementType) { this.movementType = movementType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
