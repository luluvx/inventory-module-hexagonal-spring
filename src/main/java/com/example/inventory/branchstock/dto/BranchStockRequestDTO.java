package com.example.inventory.branchstock.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record BranchStockRequestDTO(
        @NotNull(message = "El ID de la sucursal es obligatorio")
        UUID branchId,

        @NotNull(message = "El ID del producto es obligatorio")
        UUID productId,

        @NotNull(message = "El ID del lote es obligatorio")
        UUID batchId,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 0, message = "La cantidad no puede ser negativa")
        Integer quantity,

        @Min(value = 0, message = "El stock mínimo no puede ser negativo")
        Integer minimumStock
) {}
