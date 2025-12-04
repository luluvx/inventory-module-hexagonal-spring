package com.example.inventory.batch.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record BatchResponseDTO(
        UUID id,
        UUID productId,
        String productName,
        String productBrand,
        String batchNumber,
        int quantity,
        LocalDate expirationDate,
        int warningDaysBeforeExpiration,
        boolean notificationEnabled,
        boolean expired,
        boolean expiringSoon,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
