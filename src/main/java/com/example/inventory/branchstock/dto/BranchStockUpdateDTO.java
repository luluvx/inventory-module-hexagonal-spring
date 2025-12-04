package com.example.inventory.branchstock.dto;

import jakarta.validation.constraints.Min;

public record BranchStockUpdateDTO(
        @Min(value = 0, message = "La cantidad no puede ser negativa")
        Integer quantity,
        @Min(value = 0, message = "El stock mínimo no puede ser negativo")
        Integer minimumStock
) {}
