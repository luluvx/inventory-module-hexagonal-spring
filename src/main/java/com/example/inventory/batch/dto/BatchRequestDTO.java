package com.example.inventory.batch.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record BatchRequestDTO(
        @NotNull(message = "El ID del producto es obligatorio")
        UUID productId,

        @NotBlank(message = "El número de lote es obligatorio")
        String batchNumber,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        Integer quantity,

        @NotNull(message = "La fecha de vencimiento es obligatoria")
        LocalDate expirationDate,

        @Min(value = 1, message = "Los días de advertencia deben ser al menos 1")
        Integer warningDaysBeforeExpiration
) {}
