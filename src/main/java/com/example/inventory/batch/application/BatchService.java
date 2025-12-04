package com.example.inventory.batch.application;

import com.example.inventory.batch.application.mapper.BatchMapper;
import com.example.inventory.batch.domain.Batch;
import com.example.inventory.batch.dto.*;
import com.example.inventory.batch.port.in.BatchUseCase;
import com.example.inventory.batch.port.out.BatchRepository;
import com.example.inventory.product.domain.Product;
import com.example.inventory.product.port.out.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class BatchService implements BatchUseCase {

    private final BatchRepository repo;
    private final ProductRepository productRepo;
    private final BatchMapper mapper;

    public BatchService(BatchRepository repo, ProductRepository productRepo, BatchMapper mapper) {
        this.repo = repo;
        this.productRepo = productRepo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public BatchResponseDTO create(BatchRequestDTO dto) {
        Product product = productRepo.findById(dto.productId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado con id: " + dto.productId()));

        if (repo.existsByBatchNumber(dto.batchNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El número de lote ya existe: " + dto.batchNumber());
        }

        int warningDays = dto.warningDaysBeforeExpiration() != null ? dto.warningDaysBeforeExpiration() : 30;

        Batch batch = new Batch(
                null,
                dto.productId(),
                dto.batchNumber(),
                dto.quantity(),
                dto.expirationDate(),
                warningDays,
                true,
                false,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        Batch saved = repo.save(batch);
        return toResponseWithProduct(saved, product);
    }

    @Override
    @Transactional(readOnly = true)
    public BatchResponseDTO getById(UUID id) {
        Batch batch = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote no encontrado con id: " + id));
        
        Product product = productRepo.findById(batch.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto asociado no encontrado"));
        
        return toResponseWithProduct(batch, product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BatchResponseDTO> list() {
        return repo.findAll().stream().map(this::toResponseWithProductLookup).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BatchResponseDTO> listByProduct(UUID productId) {
        if (!productRepo.existsById(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado con id: " + productId);
        }
        return repo.findByProductId(productId).stream().map(this::toResponseWithProductLookup).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BatchResponseDTO> listExpiringSoon() {
        LocalDate today = LocalDate.now();
        LocalDate warningDate = today.plusDays(30);
        return repo.findExpiringSoon(warningDate).stream().map(this::toResponseWithProductLookup).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BatchResponseDTO> listExpired() {
        return repo.findExpired(LocalDate.now()).stream().map(this::toResponseWithProductLookup).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpiringBatchNotificationDTO> getExpiringNotifications() {
        LocalDate today = LocalDate.now();
        LocalDate warningDate = today.plusDays(60);
        
        return repo.findExpiringSoon(warningDate).stream()
                .filter(Batch::isNotificationEnabled)
                .map(batch -> {
                    Product product = productRepo.findById(batch.getProductId()).orElse(null);
                    long daysUntil = ChronoUnit.DAYS.between(today, batch.getExpirationDate());
                    return new ExpiringBatchNotificationDTO(
                            batch.getId(),
                            batch.getBatchNumber(),
                            batch.getProductId(),
                            product != null ? product.getName() : "N/A",
                            product != null ? product.getBrand() : "N/A",
                            batch.getExpirationDate(),
                            (int) daysUntil,
                            batch.getQuantity(),
                            batch.isNotificationEnabled()
                    );
                })
                .toList();
    }

    @Override
    @Transactional
    public BatchResponseDTO update(UUID id, BatchUpdateDTO dto) {
        Batch current = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote no encontrado con id: " + id));

        if (dto.batchNumber() != null && !dto.batchNumber().isBlank()) {
            if (!dto.batchNumber().equals(current.getBatchNumber()) && repo.existsByBatchNumberAndIdNot(dto.batchNumber(), id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El número de lote ya existe: " + dto.batchNumber());
            }
            current.setBatchNumber(dto.batchNumber());
        }

        if (dto.quantity() != null) current.setQuantity(dto.quantity());
        if (dto.expirationDate() != null) current.setExpirationDate(dto.expirationDate());
        if (dto.warningDaysBeforeExpiration() != null) current.setWarningDaysBeforeExpiration(dto.warningDaysBeforeExpiration());
        current.setUpdatedAt(LocalDateTime.now());

        Batch saved = repo.save(current);
        return toResponseWithProductLookup(saved);
    }

    @Override
    @Transactional
    public BatchResponseDTO toggleNotification(UUID id, BatchNotificationDTO dto) {
        Batch current = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote no encontrado con id: " + id));
        
        current.setNotificationEnabled(dto.notificationEnabled());
        current.setUpdatedAt(LocalDateTime.now());
        
        Batch saved = repo.save(current);
        return toResponseWithProductLookup(saved);
    }

    @Override
    @Transactional
    public void deactivateExpiredBatches() {
        int count = repo.deactivateExpiredBatches(LocalDate.now());
        if (count > 0) {
            System.out.println("Se desactivaron " + count + " lotes vencidos");
        }
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote no encontrado con id: " + id);
        }
        repo.deleteById(id);
    }

    private BatchResponseDTO toResponseWithProduct(Batch batch, Product product) {
        return new BatchResponseDTO(
                batch.getId(),
                batch.getProductId(),
                product.getName(),
                product.getBrand(),
                batch.getBatchNumber(),
                batch.getQuantity(),
                batch.getExpirationDate(),
                batch.getWarningDaysBeforeExpiration(),
                batch.isNotificationEnabled(),
                batch.isExpired(),
                batch.isExpiringSoon(),
                batch.isActive(),
                batch.getCreatedAt(),
                batch.getUpdatedAt()
        );
    }

    private BatchResponseDTO toResponseWithProductLookup(Batch batch) {
        Product product = productRepo.findById(batch.getProductId()).orElse(null);
        return new BatchResponseDTO(
                batch.getId(),
                batch.getProductId(),
                product != null ? product.getName() : "N/A",
                product != null ? product.getBrand() : "N/A",
                batch.getBatchNumber(),
                batch.getQuantity(),
                batch.getExpirationDate(),
                batch.getWarningDaysBeforeExpiration(),
                batch.isNotificationEnabled(),
                batch.isExpired(),
                batch.isExpiringSoon(),
                batch.isActive(),
                batch.getCreatedAt(),
                batch.getUpdatedAt()
        );
    }
}
