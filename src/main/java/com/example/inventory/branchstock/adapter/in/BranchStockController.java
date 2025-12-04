package com.example.inventory.branchstock.adapter.in;

import com.example.inventory.branchstock.dto.BranchStockRequestDTO;
import com.example.inventory.branchstock.dto.BranchStockResponseDTO;
import com.example.inventory.branchstock.dto.BranchStockTransferDTO;
import com.example.inventory.branchstock.dto.BranchStockUpdateDTO;
import com.example.inventory.branchstock.port.in.BranchStockUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/branch-stock")
public class BranchStockController {

    private final BranchStockUseCase useCase;

    public BranchStockController(BranchStockUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<BranchStockResponseDTO> create(@Valid @RequestBody BranchStockRequestDTO dto) {
        BranchStockResponseDTO created = useCase.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<BranchStockResponseDTO>> listAll() {
        return ResponseEntity.ok(useCase.listAll());
    }

    @GetMapping("/{stockId}")
    public ResponseEntity<BranchStockResponseDTO> getById(@PathVariable UUID stockId) {
        return ResponseEntity.ok(useCase.getById(stockId));
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<BranchStockResponseDTO>> listByBranch(@PathVariable UUID branchId) {
        return ResponseEntity.ok(useCase.listByBranch(branchId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<BranchStockResponseDTO>> listByProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok(useCase.listByProduct(productId));
    }

    @GetMapping("/batch/{batchId}")
    public ResponseEntity<List<BranchStockResponseDTO>> listByBatch(@PathVariable UUID batchId) {
        return ResponseEntity.ok(useCase.listByBatch(batchId));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<BranchStockResponseDTO>> listLowStock() {
        return ResponseEntity.ok(useCase.listLowStock());
    }

    @GetMapping("/low-stock/branch/{branchId}")
    public ResponseEntity<List<BranchStockResponseDTO>> listLowStockByBranch(@PathVariable UUID branchId) {
        return ResponseEntity.ok(useCase.listLowStockByBranch(branchId));
    }

    @PutMapping("/{stockId}")
    public ResponseEntity<BranchStockResponseDTO> update(
            @PathVariable UUID stockId,
            @Valid @RequestBody BranchStockUpdateDTO dto) {
        return ResponseEntity.ok(useCase.update(stockId, dto));
    }

    @DeleteMapping("/{stockId}")
    public ResponseEntity<Void> delete(@PathVariable UUID stockId) {
        useCase.delete(stockId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transfer")
    public ResponseEntity<BranchStockResponseDTO> transfer(@Valid @RequestBody BranchStockTransferDTO dto) {
        BranchStockResponseDTO result = useCase.transfer(dto);
        return ResponseEntity.ok(result);
    }
}
