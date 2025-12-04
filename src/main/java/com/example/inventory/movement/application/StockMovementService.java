package com.example.inventory.movement.application;

import com.example.inventory.batch.domain.Batch;
import com.example.inventory.batch.port.out.BatchRepository;
import com.example.inventory.branchstock.domain.BranchStock;
import com.example.inventory.branchstock.port.out.BranchStockRepository;
import com.example.inventory.movement.application.mapper.StockMovementMapper;
import com.example.inventory.movement.domain.MovementType;
import com.example.inventory.movement.domain.StockMovement;
import com.example.inventory.movement.dto.StockMovementRequestDTO;
import com.example.inventory.movement.dto.StockMovementResponseDTO;
import com.example.inventory.movement.dto.TransferRequestDTO;
import com.example.inventory.movement.port.in.StockMovementUseCase;
import com.example.inventory.movement.port.out.StockMovementRepository;
import com.example.inventory.product.domain.Product;
import com.example.inventory.product.port.out.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class StockMovementService implements StockMovementUseCase {

    private final StockMovementRepository repo;
    private final ProductRepository productRepo;
    private final BatchRepository batchRepo;
    private final BranchStockRepository branchStockRepo;
    private final StockMovementMapper mapper;

    public StockMovementService(StockMovementRepository repo, ProductRepository productRepo,
                                 BatchRepository batchRepo, BranchStockRepository branchStockRepo,
                                 StockMovementMapper mapper) {
        this.repo = repo;
        this.productRepo = productRepo;
        this.batchRepo = batchRepo;
        this.branchStockRepo = branchStockRepo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public StockMovementResponseDTO create(StockMovementRequestDTO dto) {
        validateProductAndBatch(dto.productId(), dto.batchId());

        StockMovement movement = new StockMovement(
                null,
                dto.productId(),
                dto.batchId(),
                dto.sourceBranchId(),
                dto.destinationBranchId(),
                dto.quantity(),
                dto.movementType(),
                dto.reason(),
                dto.performedBy(),
                LocalDateTime.now()
        );

        StockMovement saved = repo.save(movement);
        return toResponseWithDetailsLookup(saved);
    }

    @Override
    @Transactional
    public StockMovementResponseDTO transfer(TransferRequestDTO dto) {
        validateProductAndBatch(dto.productId(), dto.batchId());

        if (dto.sourceBranchId().equals(dto.destinationBranchId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La sucursal origen y destino no pueden ser la misma");
        }

        // Verificar stock en sucursal origen
        BranchStock sourceStock = branchStockRepo.findByBranchIdAndBatchId(dto.sourceBranchId(), dto.batchId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay stock de este lote en la sucursal origen"));

        if (sourceStock.getQuantity() < dto.quantity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Stock insuficiente. Disponible: " + sourceStock.getQuantity() + ", Solicitado: " + dto.quantity());
        }

        // Reducir stock en origen
        sourceStock.setQuantity(sourceStock.getQuantity() - dto.quantity());
        sourceStock.setUpdatedAt(LocalDateTime.now());
        branchStockRepo.save(sourceStock);

        // Aumentar o crear stock en destino
        BranchStock destStock = branchStockRepo.findByBranchIdAndBatchId(dto.destinationBranchId(), dto.batchId())
                .orElse(null);

        if (destStock != null) {
            destStock.setQuantity(destStock.getQuantity() + dto.quantity());
            destStock.setUpdatedAt(LocalDateTime.now());
            branchStockRepo.save(destStock);
        } else {
            BranchStock newStock = new BranchStock(
                    null,
                    dto.destinationBranchId(),
                    dto.productId(),
                    dto.batchId(),
                    dto.quantity(),
                    0,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
            branchStockRepo.save(newStock);
        }

        // Registrar el movimiento
        StockMovement movement = new StockMovement(
                null,
                dto.productId(),
                dto.batchId(),
                dto.sourceBranchId(),
                dto.destinationBranchId(),
                dto.quantity(),
                MovementType.TRANSFER,
                dto.reason() != null ? dto.reason() : "Transferencia entre sucursales",
                dto.performedBy(),
                LocalDateTime.now()
        );

        StockMovement saved = repo.save(movement);
        return toResponseWithDetailsLookup(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public StockMovementResponseDTO getById(UUID id) {
        StockMovement movement = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movimiento no encontrado con id: " + id));
        return toResponseWithDetailsLookup(movement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovementResponseDTO> list() {
        return repo.findAll().stream().map(this::toResponseWithDetailsLookup).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovementResponseDTO> listByProduct(UUID productId) {
        return repo.findByProductId(productId).stream().map(this::toResponseWithDetailsLookup).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovementResponseDTO> listByBatch(UUID batchId) {
        return repo.findByBatchId(batchId).stream().map(this::toResponseWithDetailsLookup).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovementResponseDTO> listByBranch(UUID branchId) {
        return repo.findByBranchId(branchId).stream().map(this::toResponseWithDetailsLookup).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovementResponseDTO> listByType(MovementType type) {
        return repo.findByMovementType(type).stream().map(this::toResponseWithDetailsLookup).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovementResponseDTO> listByDateRange(LocalDateTime from, LocalDateTime to) {
        return repo.findByCreatedAtBetween(from, to).stream().map(this::toResponseWithDetailsLookup).toList();
    }

    private void validateProductAndBatch(UUID productId, UUID batchId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado con id: " + productId));

        Batch batch = batchRepo.findById(batchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote no encontrado con id: " + batchId));

        if (!batch.getProductId().equals(productId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El lote no pertenece al producto especificado");
        }
    }

    private StockMovementResponseDTO toResponseWithDetailsLookup(StockMovement movement) {
        Product product = productRepo.findById(movement.getProductId()).orElse(null);
        Batch batch = batchRepo.findById(movement.getBatchId()).orElse(null);
        
        return new StockMovementResponseDTO(
                movement.getId(),
                movement.getProductId(),
                product != null ? product.getName() : "N/A",
                product != null ? product.getBrand() : "N/A",
                movement.getBatchId(),
                batch != null ? batch.getBatchNumber() : "N/A",
                movement.getSourceBranchId(),
                null, // sourceBranchName
                movement.getDestinationBranchId(),
                null, // destinationBranchName
                movement.getQuantity(),
                movement.getMovementType(),
                movement.getReason(),
                movement.getPerformedBy(),
                movement.getCreatedAt()
        );
    }
}
