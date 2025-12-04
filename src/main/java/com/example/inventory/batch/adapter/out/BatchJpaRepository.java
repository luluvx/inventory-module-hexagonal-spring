package com.example.inventory.batch.adapter.out;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BatchJpaRepository extends JpaRepository<BatchEntity, UUID> {
    List<BatchEntity> findByProductId(UUID productId);
    List<BatchEntity> findByProductIdAndActiveTrue(UUID productId);
    boolean existsByBatchNumber(String batchNumber);
    boolean existsByBatchNumberAndIdNot(String batchNumber, UUID excludeId);
    List<BatchEntity> findByActiveTrueAndExpiredFalseAndExpirationDateLessThanEqual(LocalDate warningDate);
    List<BatchEntity> findByActiveTrueAndExpirationDateLessThan(LocalDate today);
    List<BatchEntity> findByExpirationDateBetween(LocalDate from, LocalDate to);
    // Para próximos a vencer: entre hoy y fecha de advertencia (excluyendo vencidos)
    List<BatchEntity> findByActiveTrueAndExpiredFalseAndExpirationDateBetween(LocalDate today, LocalDate warningDate);
}
