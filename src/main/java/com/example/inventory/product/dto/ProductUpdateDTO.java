package com.example.inventory.product.dto;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public record ProductUpdateDTO(
        String name,
        String description,
        String sku,
        String brand,
        String category,
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
        BigDecimal unitPrice,
        String unit
) {}
