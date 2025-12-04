package com.example.inventory.movement.port.in;

import com.example.inventory.movement.dto.StockMovementRequestDTO;
import com.example.inventory.movement.dto.StockMovementResponseDTO;
import com.example.inventory.movement.dto.TransferRequestDTO;
import com.example.inventory.movement.domain.MovementType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface StockMovementUseCase {
    StockMovementResponseDTO create(StockMovementRequestDTO dto);
    StockMovementResponseDTO transfer(TransferRequestDTO dto);
    StockMovementResponseDTO getById(UUID id);
    List<StockMovementResponseDTO> list();
    List<StockMovementResponseDTO> listByProduct(UUID productId);
    List<StockMovementResponseDTO> listByBatch(UUID batchId);
    List<StockMovementResponseDTO> listByBranch(UUID branchId);
    List<StockMovementResponseDTO> listByType(MovementType type);
    List<StockMovementResponseDTO> listByDateRange(LocalDateTime from, LocalDateTime to);
}
