package com.example.inventory.branchstock.application;

import com.example.inventory.batch.domain.Batch;
import com.example.inventory.batch.port.out.BatchRepository;
import com.example.inventory.branchstock.application.mapper.BranchStockMapper;
import com.example.inventory.branchstock.domain.BranchStock;
import com.example.inventory.branchstock.dto.BranchStockRequestDTO;
import com.example.inventory.branchstock.dto.BranchStockResponseDTO;
import com.example.inventory.branchstock.dto.BranchStockTransferDTO;
import com.example.inventory.branchstock.dto.BranchStockUpdateDTO;
import com.example.inventory.branchstock.port.in.BranchStockUseCase;
import com.example.inventory.branchstock.port.out.BranchStockRepository;
import com.example.inventory.movement.domain.MovementType;
import com.example.inventory.movement.domain.StockMovement;
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
public class BranchStockService implements BranchStockUseCase {

    private final BranchStockRepository repo;
    private final ProductRepository productRepo;
    private final BatchRepository batchRepo;
    private final StockMovementRepository movementRepo;
    private final BranchStockMapper mapper;

    public BranchStockService(BranchStockRepository repo, ProductRepository productRepo, 
                               BatchRepository batchRepo, StockMovementRepository movementRepo,
                               BranchStockMapper mapper) {
        this.repo = repo;
        this.productRepo = productRepo;
        this.batchRepo = batchRepo;
        this.movementRepo = movementRepo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public BranchStockResponseDTO create(BranchStockRequestDTO dto) {
        Product product = productRepo.findById(dto.productId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado con id: " + dto.productId()));

        Batch batch = batchRepo.findById(dto.batchId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote no encontrado con id: " + dto.batchId()));

        if (!batch.getProductId().equals(dto.productId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El lote no pertenece al producto especificado");
        }

        if (repo.existsByBranchIdAndBatchId(dto.branchId(), dto.batchId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un registro de stock para esta sucursal y lote");
        }

        // Validar que la suma de asignaciones + nueva cantidad no exceda la cantidad del lote
        int totalAssigned = repo.getTotalQuantityByBatch(dto.batchId());
        int batchQuantity = batch.getQuantity();
        int newTotal = totalAssigned + dto.quantity();
        
        if (newTotal > batchQuantity) {
            int available = batchQuantity - totalAssigned;
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                String.format("Stock insuficiente en el lote. Disponible: %d, Solicitado: %d. El lote tiene %d unidades y ya hay %d asignadas a otras sucursales.", 
                    available, dto.quantity(), batchQuantity, totalAssigned));
        }

        int minStock = dto.minimumStock() != null ? dto.minimumStock() : 0;

        BranchStock stock = new BranchStock(
                null,
                dto.branchId(),
                dto.productId(),
                dto.batchId(),
                dto.quantity(),
                minStock,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        BranchStock saved = repo.save(stock);

        // Registrar movimiento de entrada (ENTRY)
        StockMovement movement = new StockMovement(
                null,
                dto.productId(),
                dto.batchId(),
                null, // sourceBranchId - null porque es entrada
                dto.branchId(), // destinationBranchId
                dto.quantity(),
                MovementType.ENTRY,
                "Asignación inicial de stock a sucursal",
                null, // performedBy
                LocalDateTime.now()
        );
        movementRepo.save(movement);

        return toResponseWithDetails(saved, product, batch);
    }

    @Override
    @Transactional(readOnly = true)
    public BranchStockResponseDTO getById(UUID id) {
        BranchStock stock = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock no encontrado con id: " + id));
        return toResponseWithDetailsLookup(stock);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchStockResponseDTO> listAll() {
        return repo.findAll().stream().map(this::toResponseWithDetailsLookup).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchStockResponseDTO> listByBranch(UUID branchId) {
        return repo.findByBranchId(branchId).stream().map(this::toResponseWithDetailsLookup).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchStockResponseDTO> listByProduct(UUID productId) {
        return repo.findByProductId(productId).stream().map(this::toResponseWithDetailsLookup).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchStockResponseDTO> listByBatch(UUID batchId) {
        return repo.findByBatchId(batchId).stream().map(this::toResponseWithDetailsLookup).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchStockResponseDTO> listLowStock() {
        return repo.findLowStock().stream().map(this::toResponseWithDetailsLookup).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchStockResponseDTO> listLowStockByBranch(UUID branchId) {
        return repo.findLowStockByBranch(branchId).stream().map(this::toResponseWithDetailsLookup).toList();
    }

    @Override
    @Transactional
    public BranchStockResponseDTO update(UUID id, BranchStockUpdateDTO dto) {
        BranchStock current = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock no encontrado con id: " + id));

        Integer oldQuantity = current.getQuantity();
        
        if (dto.quantity() != null) current.setQuantity(dto.quantity());
        if (dto.minimumStock() != null) current.setMinimumStock(dto.minimumStock());
        current.setUpdatedAt(LocalDateTime.now());

        BranchStock saved = repo.save(current);

        // Si cambió la cantidad, registrar movimiento de ajuste
        if (dto.quantity() != null && !dto.quantity().equals(oldQuantity)) {
            int difference = dto.quantity() - oldQuantity;
            MovementType type = difference > 0 ? MovementType.ENTRY : MovementType.ADJUSTMENT;
            
            StockMovement movement = new StockMovement(
                    null,
                    current.getProductId(),
                    current.getBatchId(),
                    difference < 0 ? current.getBranchId() : null, // source si es salida
                    difference > 0 ? current.getBranchId() : null, // destination si es entrada
                    Math.abs(difference),
                    type,
                    "Ajuste de inventario",
                    null,
                    LocalDateTime.now()
            );
            movementRepo.save(movement);
        }

        return toResponseWithDetailsLookup(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        BranchStock stock = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock no encontrado con id: " + id));

        // Registrar movimiento de salida (EXIT) antes de eliminar
        StockMovement movement = new StockMovement(
                null,
                stock.getProductId(),
                stock.getBatchId(),
                stock.getBranchId(), // sourceBranchId
                null, // destinationBranchId - null porque es salida
                stock.getQuantity(),
                MovementType.EXIT,
                "Eliminación de stock de sucursal",
                null,
                LocalDateTime.now()
        );
        movementRepo.save(movement);

        repo.deleteById(id);
    }

    private BranchStockResponseDTO toResponseWithDetails(BranchStock stock, Product product, Batch batch) {
        return new BranchStockResponseDTO(
                stock.getId(),
                stock.getBranchId(),
                null, // branchName - se obtendría del servicio de sucursales
                stock.getProductId(),
                product.getName(),
                product.getBrand(),
                stock.getBatchId(),
                batch.getBatchNumber(),
                stock.getQuantity(),
                stock.getMinimumStock(),
                stock.isLowStock(),
                stock.getCreatedAt(),
                stock.getUpdatedAt()
        );
    }

    private BranchStockResponseDTO toResponseWithDetailsLookup(BranchStock stock) {
        Product product = productRepo.findById(stock.getProductId()).orElse(null);
        Batch batch = batchRepo.findById(stock.getBatchId()).orElse(null);
        
        return new BranchStockResponseDTO(
                stock.getId(),
                stock.getBranchId(),
                null,
                stock.getProductId(),
                product != null ? product.getName() : "N/A",
                product != null ? product.getBrand() : "N/A",
                stock.getBatchId(),
                batch != null ? batch.getBatchNumber() : "N/A",
                stock.getQuantity(),
                stock.getMinimumStock(),
                stock.isLowStock(),
                stock.getCreatedAt(),
                stock.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public BranchStockResponseDTO transfer(BranchStockTransferDTO dto) {
        // Obtener el stock origen
        BranchStock sourceStock = repo.findById(dto.sourceStockId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Stock origen no encontrado con id: " + dto.sourceStockId()));

        // Validar que la sucursal destino sea diferente a la origen
        if (sourceStock.getBranchId().equals(dto.targetBranchId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "No puedes transferir a la misma sucursal");
        }

        // Validar cantidad a transferir
        if (dto.quantity() > sourceStock.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                String.format("Cantidad insuficiente. Disponible: %d, Solicitado: %d", 
                    sourceStock.getQuantity(), dto.quantity()));
        }

        // Obtener producto y lote para la respuesta
        Product product = productRepo.findById(sourceStock.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        Batch batch = batchRepo.findById(sourceStock.getBatchId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote no encontrado"));

        // Guardar el branchId origen antes de posiblemente eliminarlo
        UUID sourceBranchId = sourceStock.getBranchId();

        // Buscar o crear stock en destino
        BranchStock targetStock = repo.findByBranchIdAndBatchId(dto.targetBranchId(), sourceStock.getBatchId())
                .orElse(null);

        if (targetStock == null) {
            // Crear nuevo stock en destino
            targetStock = new BranchStock(
                    null,
                    dto.targetBranchId(),
                    sourceStock.getProductId(),
                    sourceStock.getBatchId(),
                    dto.quantity(),
                    sourceStock.getMinimumStock(),
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
        } else {
            // Sumar cantidad al stock existente
            targetStock.setQuantity(targetStock.getQuantity() + dto.quantity());
            targetStock.setUpdatedAt(LocalDateTime.now());
        }

        // Restar del stock origen
        int newSourceQuantity = sourceStock.getQuantity() - dto.quantity();
        
        if (newSourceQuantity == 0) {
            // Si queda en 0, eliminar el stock origen
            repo.deleteById(sourceStock.getId());
        } else {
            // Actualizar con nueva cantidad
            sourceStock.setQuantity(newSourceQuantity);
            sourceStock.setUpdatedAt(LocalDateTime.now());
            repo.save(sourceStock);
        }

        // Guardar stock destino
        BranchStock savedTarget = repo.save(targetStock);

        // Registrar movimiento de transferencia (TRANSFER)
        StockMovement movement = new StockMovement(
                null,
                sourceStock.getProductId(),
                sourceStock.getBatchId(),
                sourceBranchId, // sourceBranchId
                dto.targetBranchId(), // destinationBranchId
                dto.quantity(),
                MovementType.TRANSFER,
                "Transferencia entre sucursales",
                null, // performedBy
                LocalDateTime.now()
        );
        movementRepo.save(movement);
        
        return toResponseWithDetails(savedTarget, product, batch);
    }
}
