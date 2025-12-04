package com.example.inventory.branchstock.port.in;

import com.example.inventory.branchstock.dto.BranchStockRequestDTO;
import com.example.inventory.branchstock.dto.BranchStockResponseDTO;
import com.example.inventory.branchstock.dto.BranchStockUpdateDTO;

import java.util.List;
import java.util.UUID;

public interface BranchStockUseCase {
    BranchStockResponseDTO create(BranchStockRequestDTO dto);
    BranchStockResponseDTO getById(UUID id);
    List<BranchStockResponseDTO> listByBranch(UUID branchId);
    List<BranchStockResponseDTO> listByProduct(UUID productId);
    List<BranchStockResponseDTO> listByBatch(UUID batchId);
    List<BranchStockResponseDTO> listLowStock();
    List<BranchStockResponseDTO> listLowStockByBranch(UUID branchId);
    BranchStockResponseDTO update(UUID id, BranchStockUpdateDTO dto);
    void delete(UUID id);
}
