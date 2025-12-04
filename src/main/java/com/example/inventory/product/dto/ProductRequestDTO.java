package com.example.inventory.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ProductRequestDTO(
        @NotBlank(message = "El nombre es obligatorio")
        String name,

        String description,

        @NotBlank(message = "El SKU es obligatorio")
        String sku,

        @NotBlank(message = "La marca es obligatoria")
        String brand,

        String category,

        @NotNull(message = "El precio unitario es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
        BigDecimal unitPrice,

        @NotBlank(message = "La unidad de medida es obligatoria")
        String unit
) {}
