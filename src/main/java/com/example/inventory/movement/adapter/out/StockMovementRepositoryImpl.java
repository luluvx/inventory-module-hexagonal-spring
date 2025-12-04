package com.example.inventory.movement.adapter.out;

import com.example.inventory.movement.application.mapper.StockMovementMapper;
import com.example.inventory.movement.domain.MovementType;
import com.example.inventory.movement.domain.StockMovement;
import com.example.inventory.movement.port.out.StockMovementRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class StockMovementRepositoryImpl implements StockMovementRepository {

    private final StockMovementJpaRepository jpa;
    private final StockMovementMapper mapper;

    public StockMovementRepositoryImpl(StockMovementJpaRepository jpa, StockMovementMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public StockMovement save(StockMovement movement) {
        StockMovementEntity entity = mapper.domainToEntity(movement);
        StockMovementEntity saved = jpa.save(entity);
        return mapper.entityToDomain(saved);
    }

    @Override
    public Optional<StockMovement> findById(UUID id) {
        return jpa.findById(id).map(mapper::entityToDomain);
    }

    @Override
    public List<StockMovement> findAll() {
        return jpa.findAll().stream().map(mapper::entityToDomain).toList();
    }

    @Override
    public List<StockMovement> findByProductId(UUID productId) {
        return jpa.findByProductId(productId).stream().map(mapper::entityToDomain).toList();
    }

    @Override
    public List<StockMovement> findByBatchId(UUID batchId) {
        return jpa.findByBatchId(batchId).stream().map(mapper::entityToDomain).toList();
    }

    @Override
    public List<StockMovement> findBySourceBranchId(UUID branchId) {
        return jpa.findBySourceBranchId(branchId).stream().map(mapper::entityToDomain).toList();
    }

    @Override
    public List<StockMovement> findByDestinationBranchId(UUID branchId) {
        return jpa.findByDestinationBranchId(branchId).stream().map(mapper::entityToDomain).toList();
    }

    @Override
    public List<StockMovement> findByBranchId(UUID branchId) {
        return jpa.findBySourceBranchIdOrDestinationBranchIdOrderByCreatedAtDesc(branchId, branchId)
                .stream().map(mapper::entityToDomain).toList();
    }

    @Override
    public List<StockMovement> findByMovementType(MovementType type) {
        return jpa.findByMovementType(type).stream().map(mapper::entityToDomain).toList();
    }

    @Override
    public List<StockMovement> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to) {
        return jpa.findByCreatedAtBetween(from, to).stream().map(mapper::entityToDomain).toList();
    }

    @Override
    public List<StockMovement> findByBranchIdAndCreatedAtBetween(UUID branchId, LocalDateTime from, LocalDateTime to) {
        List<StockMovementEntity> source = jpa.findBySourceBranchIdAndCreatedAtBetweenOrderByCreatedAtDesc(branchId, from, to);
        List<StockMovementEntity> dest = jpa.findByDestinationBranchIdAndCreatedAtBetweenOrderByCreatedAtDesc(branchId, from, to);
        java.util.Set<UUID> ids = new java.util.HashSet<>();
        java.util.List<StockMovement> result = new java.util.ArrayList<>();
        for (StockMovementEntity e : source) {
            if (ids.add(e.getId())) result.add(mapper.entityToDomain(e));
        }
        for (StockMovementEntity e : dest) {
            if (ids.add(e.getId())) result.add(mapper.entityToDomain(e));
        }
        return result;
    }

    @Override
    public List<StockMovement> findByBranchIdAndMovementTypeAndCreatedAtBetween(UUID branchId, MovementType type, LocalDateTime from, LocalDateTime to) {
        List<StockMovementEntity> source = jpa.findBySourceBranchIdAndMovementTypeAndCreatedAtBetweenOrderByCreatedAtDesc(branchId, type, from, to);
        List<StockMovementEntity> dest = jpa.findByDestinationBranchIdAndMovementTypeAndCreatedAtBetweenOrderByCreatedAtDesc(branchId, type, from, to);
        java.util.Set<UUID> ids = new java.util.HashSet<>();
        java.util.List<StockMovement> result = new java.util.ArrayList<>();
        for (StockMovementEntity e : source) {
            if (ids.add(e.getId())) result.add(mapper.entityToDomain(e));
        }
        for (StockMovementEntity e : dest) {
            if (ids.add(e.getId())) result.add(mapper.entityToDomain(e));
        }
        return result;
    }

    @Override
    public List<StockMovement> findByProductIdAndCreatedAtBetween(UUID productId, LocalDateTime from, LocalDateTime to) {
        return jpa.findByProductIdAndCreatedAtBetweenOrderByCreatedAtDesc(productId, from, to)
                .stream().map(mapper::entityToDomain).toList();
    }
}
