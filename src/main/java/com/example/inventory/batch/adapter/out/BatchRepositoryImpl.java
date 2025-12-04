package com.example.inventory.batch.adapter.out;

import com.example.inventory.batch.application.mapper.BatchMapper;
import com.example.inventory.batch.domain.Batch;
import com.example.inventory.batch.port.out.BatchRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BatchRepositoryImpl implements BatchRepository {

    private final BatchJpaRepository jpa;
    private final BatchMapper mapper;

    public BatchRepositoryImpl(BatchJpaRepository jpa, BatchMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Batch save(Batch batch) {
        BatchEntity entity = mapper.domainToEntity(batch);
        BatchEntity saved = jpa.save(entity);
        return mapper.entityToDomain(saved);
    }

    @Override
    public Optional<Batch> findById(UUID id) {
        return jpa.findById(id).map(mapper::entityToDomain);
    }

    @Override
    public List<Batch> findAll() {
        return jpa.findAll().stream().map(mapper::entityToDomain).toList();
    }

    @Override
    public List<Batch> findByProductId(UUID productId) {
        return jpa.findByProductId(productId).stream().map(mapper::entityToDomain).toList();
    }

    @Override
    public List<Batch> findExpiringSoon(LocalDate warningDate) {
        return jpa.findByActiveTrueAndExpiredFalseAndExpirationDateLessThanEqual(warningDate)
                .stream().map(mapper::entityToDomain).toList();
    }

    @Override
    public List<Batch> findExpired(LocalDate today) {
        return jpa.findByActiveTrueAndExpirationDateLessThan(today)
                .stream().map(mapper::entityToDomain).toList();
    }

    @Override
    public List<Batch> findByExpirationDateBetween(LocalDate from, LocalDate to) {
        return jpa.findByExpirationDateBetween(from, to).stream().map(mapper::entityToDomain).toList();
    }

    @Override
    public List<Batch> findActiveByProductId(UUID productId) {
        return jpa.findByProductIdAndActiveTrue(productId).stream().map(mapper::entityToDomain).toList();
    }

    @Override
    public boolean existsByBatchNumber(String batchNumber) {
        return jpa.existsByBatchNumber(batchNumber);
    }

    @Override
    public boolean existsByBatchNumberAndIdNot(String batchNumber, UUID excludeId) {
        return jpa.existsByBatchNumberAndIdNot(batchNumber, excludeId);
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpa.existsById(id);
    }

    @Override
    @Transactional
    public int deactivateExpiredBatches(LocalDate today) {
        List<BatchEntity> expiredBatches = jpa.findByActiveTrueAndExpirationDateLessThan(today);
        int count = 0;
        for (BatchEntity batch : expiredBatches) {
            batch.setExpired(true);
            batch.setActive(false);
            batch.setUpdatedAt(java.time.LocalDateTime.now());
            jpa.save(batch);
            count++;
        }
        return count;
    }
}
