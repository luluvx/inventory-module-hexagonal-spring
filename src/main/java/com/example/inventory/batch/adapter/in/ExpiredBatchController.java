package com.example.inventory.batch.adapter.in;

import com.example.inventory.batch.application.ExpiredBatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/batches/expired")
public class ExpiredBatchController {

    private final ExpiredBatchService expiredBatchService;

    public ExpiredBatchController(ExpiredBatchService expiredBatchService) {
        this.expiredBatchService = expiredBatchService;
    }

    /**
     * Procesa todos los lotes vencidos (baja automática)
     */
    @PostMapping("/process-all")
    public ResponseEntity<String> processAllExpiredBatches() {
        expiredBatchService.processExpiredBatches();
        return ResponseEntity.ok("Proceso de baja de lotes vencidos completado");
    }

    /**
     * Da de baja un lote específico por vencimiento
     */
    @PostMapping("/write-off/{batchId}")
    public ResponseEntity<String> writeOffBatch(@PathVariable UUID batchId) {
        expiredBatchService.writeOffBatchById(batchId);
        return ResponseEntity.ok("Lote dado de baja exitosamente");
    }
}
