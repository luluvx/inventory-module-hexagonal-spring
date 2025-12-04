package com.example.inventory.branchstock.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BranchStockTransferDTO(
        @NotNull(message = "El ID del stock origen es requerido")
        UUID sourceStockId,
        
        @NotNull(message = "La sucursal destino es requerida")
        UUID targetBranchId,
        
        @NotNull(message = "La cantidad a transferir es requerida")
        @Min(value = 1, message = "La cantidad a transferir debe ser mayor a 0")
        Integer quantity
) {}
