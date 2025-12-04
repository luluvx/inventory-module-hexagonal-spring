package com.example.inventory.branchstock.application;

import com.example.inventory.batch.domain.Batch;
import com.example.inventory.batch.port.out.BatchRepository;
import com.example.inventory.branchstock.application.mapper.BranchStockMapper;
import com.example.inventory.branchstock.domain.BranchStock;
import com.example.inventory.branchstock.dto.BranchStockRequestDTO;
import com.example.inventory.branchstock.dto.BranchStockResponseDTO;
import com.example.inventory.branchstock.dto.BranchStockUpdateDTO;
import com.example.inventory.branchstock.port.in.BranchStockUseCase;
import com.example.inventory.branchstock.port.out.BranchStockRepository;
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
    private final BranchStockMapper mapper;

    public BranchStockService(BranchStockRepository repo, ProductRepository productRepo, 
                               BatchRepository batchRepo, BranchStockMapper mapper) {
        this.repo = repo;
        this.productRepo = productRepo;
        this.batchRepo = batchRepo;
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

        if (dto.quantity() != null) current.setQuantity(dto.quantity());
        if (dto.minimumStock() != null) current.setMinimumStock(dto.minimumStock());
        current.setUpdatedAt(LocalDateTime.now());

        BranchStock saved = repo.save(current);
        return toResponseWithDetailsLookup(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock no encontrado con id: " + id);
        }
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
}
