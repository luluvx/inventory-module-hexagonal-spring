package com.example.inventory.report.dto;

import java.util.UUID;

public record InventoryCountDTO(
        UUID branchId,
        String branchName,
        int totalProducts,
        int totalBatches,
        int totalQuantity,
        int lowStockItems,
        int expiringBatches,
        int expiredBatches
) {}
