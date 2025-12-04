package com.example.inventory.report.dto;

import com.example.inventory.movement.domain.MovementType;
import java.time.LocalDateTime;
import java.util.UUID;

public record MovementReportDTO(
        UUID movementId,
        UUID productId,
        String productName,
        String productBrand,
        UUID batchId,
        String batchNumber,
        UUID sourceBranchId,
        String sourceBranchName,
        UUID destinationBranchId,
        String destinationBranchName,
        int quantity,
        MovementType movementType,
        String reason,
        String performedBy,
        LocalDateTime createdAt
) {}
