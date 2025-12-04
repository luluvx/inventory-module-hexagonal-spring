package com.example.inventory.batch.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ExpiringBatchNotificationDTO(
        UUID batchId,
        String batchNumber,
        UUID productId,
        String productName,
        String productBrand,
        LocalDate expirationDate,
        int daysUntilExpiration,
        int quantity,
        boolean notificationEnabled
) {}
