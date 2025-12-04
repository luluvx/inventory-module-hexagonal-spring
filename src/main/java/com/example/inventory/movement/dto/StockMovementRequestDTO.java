package com.example.inventory.movement.dto;

import com.example.inventory.movement.domain.MovementType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record StockMovementRequestDTO(
        @NotNull(message = "El ID del producto es obligatorio")
        UUID productId,

        @NotNull(message = "El ID del lote es obligatorio")
        UUID batchId,

        UUID sourceBranchId,

        UUID destinationBranchId,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        Integer quantity,

        @NotNull(message = "El tipo de movimiento es obligatorio")
        MovementType movementType,

        String reason,

        String performedBy
) {}
