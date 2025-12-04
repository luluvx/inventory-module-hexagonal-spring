package com.example.inventory.batch.adapter.in;

import com.example.inventory.batch.dto.*;
import com.example.inventory.batch.port.in.BatchUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/batches")
public class BatchController {

    private final BatchUseCase useCase;

    public BatchController(BatchUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<BatchResponseDTO> create(@Valid @RequestBody BatchRequestDTO dto) {
        BatchResponseDTO created = useCase.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<BatchResponseDTO>> list() {
        return ResponseEntity.ok(useCase.list());
    }

    @GetMapping("/{batchId}")
    public ResponseEntity<BatchResponseDTO> getById(@PathVariable UUID batchId) {
        return ResponseEntity.ok(useCase.getById(batchId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<BatchResponseDTO>> listByProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok(useCase.listByProduct(productId));
    }

    @GetMapping("/expiring-soon")
    public ResponseEntity<List<BatchResponseDTO>> listExpiringSoon() {
        return ResponseEntity.ok(useCase.listExpiringSoon());
    }

    @GetMapping("/expired")
    public ResponseEntity<List<BatchResponseDTO>> listExpired() {
        return ResponseEntity.ok(useCase.listExpired());
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<ExpiringBatchNotificationDTO>> getExpiringNotifications() {
        return ResponseEntity.ok(useCase.getExpiringNotifications());
    }

    @PutMapping("/{batchId}")
    public ResponseEntity<BatchResponseDTO> update(
            @PathVariable UUID batchId,
            @Valid @RequestBody BatchUpdateDTO dto) {
        return ResponseEntity.ok(useCase.update(batchId, dto));
    }

    @PatchMapping("/{batchId}/notification")
    public ResponseEntity<BatchResponseDTO> toggleNotification(
            @PathVariable UUID batchId,
            @Valid @RequestBody BatchNotificationDTO dto) {
        return ResponseEntity.ok(useCase.toggleNotification(batchId, dto));
    }

    @PostMapping("/deactivate-expired")
    public ResponseEntity<Void> deactivateExpiredBatches() {
        useCase.deactivateExpiredBatches();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{batchId}")
    public ResponseEntity<Void> delete(@PathVariable UUID batchId) {
        useCase.delete(batchId);
        return ResponseEntity.noContent().build();
    }
}
