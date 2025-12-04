package com.example.inventory.movement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record TransferRequestDTO(
        @NotNull(message = "El ID del producto es obligatorio")
        UUID productId,

        @NotNull(message = "El ID del lote es obligatorio")
        UUID batchId,

        @NotNull(message = "El ID de la sucursal origen es obligatorio")
        UUID sourceBranchId,

        @NotNull(message = "El ID de la sucursal destino es obligatorio")
        UUID destinationBranchId,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        Integer quantity,

        String reason,

        String performedBy
) {}
