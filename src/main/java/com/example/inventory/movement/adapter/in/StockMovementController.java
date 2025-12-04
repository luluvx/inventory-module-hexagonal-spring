package com.example.inventory.movement.adapter.in;

import com.example.inventory.movement.domain.MovementType;
import com.example.inventory.movement.dto.StockMovementRequestDTO;
import com.example.inventory.movement.dto.StockMovementResponseDTO;
import com.example.inventory.movement.dto.TransferRequestDTO;
import com.example.inventory.movement.port.in.StockMovementUseCase;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/movements")
public class StockMovementController {

    private final StockMovementUseCase useCase;

    public StockMovementController(StockMovementUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<StockMovementResponseDTO> create(@Valid @RequestBody StockMovementRequestDTO dto) {
        StockMovementResponseDTO created = useCase.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/transfer")
    public ResponseEntity<StockMovementResponseDTO> transfer(@Valid @RequestBody TransferRequestDTO dto) {
        StockMovementResponseDTO created = useCase.transfer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<StockMovementResponseDTO>> list() {
        return ResponseEntity.ok(useCase.list());
    }

    @GetMapping("/{movementId}")
    public ResponseEntity<StockMovementResponseDTO> getById(@PathVariable UUID movementId) {
        return ResponseEntity.ok(useCase.getById(movementId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<StockMovementResponseDTO>> listByProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok(useCase.listByProduct(productId));
    }

    @GetMapping("/batch/{batchId}")
    public ResponseEntity<List<StockMovementResponseDTO>> listByBatch(@PathVariable UUID batchId) {
        return ResponseEntity.ok(useCase.listByBatch(batchId));
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<StockMovementResponseDTO>> listByBranch(@PathVariable UUID branchId) {
        return ResponseEntity.ok(useCase.listByBranch(branchId));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<StockMovementResponseDTO>> listByType(@PathVariable MovementType type) {
        return ResponseEntity.ok(useCase.listByType(type));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<StockMovementResponseDTO>> listByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(useCase.listByDateRange(from, to));
    }
}
