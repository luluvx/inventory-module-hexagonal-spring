package com.example.inventory.batch.dto;

import jakarta.validation.constraints.Min;
import java.time.LocalDate;

public record BatchUpdateDTO(
        String batchNumber,
        @Min(value = 0, message = "La cantidad no puede ser negativa")
        Integer quantity,
        LocalDate expirationDate,
        @Min(value = 1, message = "Los días de advertencia deben ser al menos 1")
        Integer warningDaysBeforeExpiration
) {}
