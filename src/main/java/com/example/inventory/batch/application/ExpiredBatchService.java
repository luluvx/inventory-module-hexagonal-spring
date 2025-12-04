package com.example.inventory.batch.application;

import com.example.inventory.batch.domain.Batch;
import com.example.inventory.batch.port.out.BatchRepository;
import com.example.inventory.branchstock.domain.BranchStock;
import com.example.inventory.branchstock.port.out.BranchStockRepository;
import com.example.inventory.movement.domain.MovementType;
import com.example.inventory.movement.domain.StockMovement;
import com.example.inventory.movement.port.out.StockMovementRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExpiredBatchService {

    private final BatchRepository batchRepo;
    private final BranchStockRepository branchStockRepo;
    private final StockMovementRepository movementRepo;

    public ExpiredBatchService(BatchRepository batchRepo, BranchStockRepository branchStockRepo,
                                StockMovementRepository movementRepo) {
        this.batchRepo = batchRepo;
        this.branchStockRepo = branchStockRepo;
        this.movementRepo = movementRepo;
    }

    /**
     * Proceso automático para dar de baja lotes vencidos
     * Se ejecuta todos los días a las 00:00
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void processExpiredBatches() {
        LocalDate today = LocalDate.now();
        List<Batch> expiredBatches = batchRepo.findExpired(today);

        for (Batch batch : expiredBatches) {
            if (!batch.isExpired() && batch.isActive()) {
                writeOffExpiredBatch(batch);
            }
        }
    }

    /**
     * Da de baja un lote vencido específico
     */
    @Transactional
    public void writeOffExpiredBatch(Batch batch) {
        // Obtener todo el stock de este lote en todas las sucursales
        List<BranchStock> stockList = branchStockRepo.findByBatchId(batch.getId());

        for (BranchStock stock : stockList) {
            if (stock.getQuantity() > 0) {
                // Registrar movimiento de baja por vencimiento
                StockMovement movement = new StockMovement(
                        null,
                        stock.getProductId(),
                        batch.getId(),
                        stock.getBranchId(),
                        null,
                        stock.getQuantity(),
                        MovementType.EXPIRED_WRITE_OFF,
                        "Baja automática por vencimiento de lote",
                        "SISTEMA",
                        LocalDateTime.now()
                );
                movementRepo.save(movement);

                // Poner stock en cero
                stock.setQuantity(0);
                stock.setUpdatedAt(LocalDateTime.now());
                branchStockRepo.save(stock);
            }
        }

        // Marcar lote como vencido e inactivo
        batch.setExpired(true);
        batch.setActive(false);
        batch.setUpdatedAt(LocalDateTime.now());
        batchRepo.save(batch);
    }

    /**
     * Da de baja manualmente un lote vencido por ID
     */
    @Transactional
    public void writeOffBatchById(java.util.UUID batchId) {
        Batch batch = batchRepo.findById(batchId)
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado con id: " + batchId));
        
        writeOffExpiredBatch(batch);
    }
}
