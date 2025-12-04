package com.example.inventory.report.dto;

import java.util.UUID;

public record StockByBranchDTO(
        UUID branchId,
        String branchName,
        UUID productId,
        String productName,
        String productBrand,
        String productSku,
        int totalQuantity,
        int activeBatches
) {}
