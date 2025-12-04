package com.example.inventory.batch.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Batch {
    private UUID id;
    private UUID productId;
    private String batchNumber;
    private int quantity;
    private LocalDate expirationDate;
    private int warningDaysBeforeExpiration;
    private boolean notificationEnabled;
    private boolean expired;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Batch() {}

    public Batch(UUID id, UUID productId, String batchNumber, int quantity,
                 LocalDate expirationDate, int warningDaysBeforeExpiration,
                 boolean notificationEnabled, boolean expired, boolean active,
                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.productId = productId;
        this.batchNumber = batchNumber;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.warningDaysBeforeExpiration = warningDaysBeforeExpiration;
        this.notificationEnabled = notificationEnabled;
        this.expired = expired;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }

    public int getWarningDaysBeforeExpiration() { return warningDaysBeforeExpiration; }
    public void setWarningDaysBeforeExpiration(int warningDaysBeforeExpiration) { this.warningDaysBeforeExpiration = warningDaysBeforeExpiration; }

    public boolean isNotificationEnabled() { return notificationEnabled; }
    public void setNotificationEnabled(boolean notificationEnabled) { this.notificationEnabled = notificationEnabled; }

    public boolean isExpired() { return expired; }
    public void setExpired(boolean expired) { this.expired = expired; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isExpiringSoon() {
        if (expirationDate == null) return false;
        LocalDate warningDate = expirationDate.minusDays(warningDaysBeforeExpiration);
        return LocalDate.now().isAfter(warningDate) || LocalDate.now().isEqual(warningDate);
    }

    public boolean isExpiredNow() {
        if (expirationDate == null) return false;
        return LocalDate.now().isAfter(expirationDate);
    }
}
