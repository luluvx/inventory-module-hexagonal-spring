package com.example.inventory.branchstock.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class BranchStock {
    private UUID id;
    private UUID branchId;
    private UUID productId;
    private UUID batchId;
    private int quantity;
    private int minimumStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BranchStock() {}

    public BranchStock(UUID id, UUID branchId, UUID productId, UUID batchId,
                       int quantity, int minimumStock,
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.branchId = branchId;
        this.productId = productId;
        this.batchId = batchId;
        this.quantity = quantity;
        this.minimumStock = minimumStock;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getBranchId() { return branchId; }
    public void setBranchId(UUID branchId) { this.branchId = branchId; }

    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }

    public UUID getBatchId() { return batchId; }
    public void setBatchId(UUID batchId) { this.batchId = batchId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getMinimumStock() { return minimumStock; }
    public void setMinimumStock(int minimumStock) { this.minimumStock = minimumStock; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isLowStock() {
        return quantity <= minimumStock;
    }
}
