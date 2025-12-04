package com.example.inventory.batch.port.out;

import com.example.inventory.batch.domain.Batch;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BatchRepository {
    Batch save(Batch batch);
    Optional<Batch> findById(UUID id);
    List<Batch> findAll();
    List<Batch> findByProductId(UUID productId);
    List<Batch> findExpiringSoon(LocalDate warningDate);
    List<Batch> findExpired(LocalDate today);
    List<Batch> findByExpirationDateBetween(LocalDate from, LocalDate to);
    List<Batch> findActiveByProductId(UUID productId);
    boolean existsByBatchNumber(String batchNumber);
    boolean existsByBatchNumberAndIdNot(String batchNumber, UUID excludeId);
    void deleteById(UUID id);
    boolean existsById(UUID id);
    int deactivateExpiredBatches(LocalDate today);
}
