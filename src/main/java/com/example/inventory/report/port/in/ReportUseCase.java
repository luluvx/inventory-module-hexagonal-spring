package com.example.inventory.report.port.in;

import com.example.inventory.report.dto.InventoryCountDTO;
import com.example.inventory.report.dto.MovementReportDTO;
import com.example.inventory.report.dto.StockByBranchDTO;
import com.example.inventory.movement.domain.MovementType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReportUseCase {
    List<StockByBranchDTO> getStockByBranch(UUID branchId);
    List<StockByBranchDTO> getAllStockByBranches();
    InventoryCountDTO getInventoryCount(UUID branchId);
    List<InventoryCountDTO> getAllInventoryCounts();
    List<MovementReportDTO> getMovementReport(UUID branchId, LocalDateTime from, LocalDateTime to);
    List<MovementReportDTO> getMovementReportByType(UUID branchId, MovementType type, LocalDateTime from, LocalDateTime to);
    List<MovementReportDTO> getMovementReportByProduct(UUID productId, LocalDateTime from, LocalDateTime to);
}
