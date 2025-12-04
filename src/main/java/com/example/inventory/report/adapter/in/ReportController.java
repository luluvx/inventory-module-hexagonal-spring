package com.example.inventory.report.adapter.in;

import com.example.inventory.movement.domain.MovementType;
import com.example.inventory.report.dto.InventoryCountDTO;
import com.example.inventory.report.dto.MovementReportDTO;
import com.example.inventory.report.dto.StockByBranchDTO;
import com.example.inventory.report.port.in.ReportUseCase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportUseCase useCase;

    public ReportController(ReportUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/stock/branch/{branchId}")
    public ResponseEntity<List<StockByBranchDTO>> getStockByBranch(@PathVariable UUID branchId) {
        return ResponseEntity.ok(useCase.getStockByBranch(branchId));
    }

    @GetMapping("/stock/all-branches")
    public ResponseEntity<List<StockByBranchDTO>> getAllStockByBranches() {
        return ResponseEntity.ok(useCase.getAllStockByBranches());
    }

    @GetMapping("/inventory-count/branch/{branchId}")
    public ResponseEntity<InventoryCountDTO> getInventoryCount(@PathVariable UUID branchId) {
        return ResponseEntity.ok(useCase.getInventoryCount(branchId));
    }

    @GetMapping("/inventory-count/all-branches")
    public ResponseEntity<List<InventoryCountDTO>> getAllInventoryCounts() {
        return ResponseEntity.ok(useCase.getAllInventoryCounts());
    }

    @GetMapping("/movements/branch/{branchId}")
    public ResponseEntity<List<MovementReportDTO>> getMovementReport(
            @PathVariable UUID branchId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(useCase.getMovementReport(branchId, from, to));
    }

    @GetMapping("/movements/branch/{branchId}/type/{type}")
    public ResponseEntity<List<MovementReportDTO>> getMovementReportByType(
            @PathVariable UUID branchId,
            @PathVariable MovementType type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(useCase.getMovementReportByType(branchId, type, from, to));
    }

    @GetMapping("/movements/product/{productId}")
    public ResponseEntity<List<MovementReportDTO>> getMovementReportByProduct(
            @PathVariable UUID productId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(useCase.getMovementReportByProduct(productId, from, to));
    }
}
