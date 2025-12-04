package com.example.inventory.report.application;

import com.example.inventory.batch.domain.Batch;
import com.example.inventory.batch.port.out.BatchRepository;
import com.example.inventory.branchstock.domain.BranchStock;
import com.example.inventory.branchstock.port.out.BranchStockRepository;
import com.example.inventory.movement.domain.MovementType;
import com.example.inventory.movement.domain.StockMovement;
import com.example.inventory.movement.port.out.StockMovementRepository;
import com.example.inventory.product.domain.Product;
import com.example.inventory.product.port.out.ProductRepository;
import com.example.inventory.report.dto.InventoryCountDTO;
import com.example.inventory.report.dto.MovementReportDTO;
import com.example.inventory.report.dto.StockByBranchDTO;
import com.example.inventory.report.port.in.ReportUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService implements ReportUseCase {

    private final BranchStockRepository branchStockRepo;
    private final ProductRepository productRepo;
    private final BatchRepository batchRepo;
    private final StockMovementRepository movementRepo;

    public ReportService(BranchStockRepository branchStockRepo, ProductRepository productRepo,
                         BatchRepository batchRepo, StockMovementRepository movementRepo) {
        this.branchStockRepo = branchStockRepo;
        this.productRepo = productRepo;
        this.batchRepo = batchRepo;
        this.movementRepo = movementRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockByBranchDTO> getStockByBranch(UUID branchId) {
        List<BranchStock> stocks = branchStockRepo.findByBranchId(branchId);
        
        Map<UUID, List<BranchStock>> byProduct = stocks.stream()
                .collect(Collectors.groupingBy(BranchStock::getProductId));

        List<StockByBranchDTO> result = new ArrayList<>();
        
        for (Map.Entry<UUID, List<BranchStock>> entry : byProduct.entrySet()) {
            UUID productId = entry.getKey();
            List<BranchStock> productStocks = entry.getValue();
            
            Product product = productRepo.findById(productId).orElse(null);
            if (product == null) continue;
            
            int totalQty = productStocks.stream().mapToInt(BranchStock::getQuantity).sum();
            int activeBatches = productStocks.size();
            
            result.add(new StockByBranchDTO(
                    branchId,
                    null, // branchName
                    productId,
                    product.getName(),
                    product.getBrand(),
                    product.getSku(),
                    totalQty,
                    activeBatches
            ));
        }
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockByBranchDTO> getAllStockByBranches() {
        List<Product> allProducts = productRepo.findAll();
        Map<UUID, Product> productMap = allProducts.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        List<BranchStock> allStocks = new ArrayList<>();
        // Obtener todos los stocks agrupados por sucursal
        Set<UUID> branchIds = new HashSet<>();
        
        for (Product product : allProducts) {
            List<BranchStock> productStocks = branchStockRepo.findByProductId(product.getId());
            allStocks.addAll(productStocks);
            productStocks.forEach(s -> branchIds.add(s.getBranchId()));
        }

        List<StockByBranchDTO> result = new ArrayList<>();
        
        Map<UUID, Map<UUID, List<BranchStock>>> byBranchThenProduct = allStocks.stream()
                .collect(Collectors.groupingBy(
                        BranchStock::getBranchId,
                        Collectors.groupingBy(BranchStock::getProductId)
                ));

        for (Map.Entry<UUID, Map<UUID, List<BranchStock>>> branchEntry : byBranchThenProduct.entrySet()) {
            UUID branchId = branchEntry.getKey();
            
            for (Map.Entry<UUID, List<BranchStock>> productEntry : branchEntry.getValue().entrySet()) {
                UUID productId = productEntry.getKey();
                List<BranchStock> stocks = productEntry.getValue();
                Product product = productMap.get(productId);
                
                if (product == null) continue;
                
                int totalQty = stocks.stream().mapToInt(BranchStock::getQuantity).sum();
                int activeBatches = stocks.size();
                
                result.add(new StockByBranchDTO(
                        branchId,
                        null,
                        productId,
                        product.getName(),
                        product.getBrand(),
                        product.getSku(),
                        totalQty,
                        activeBatches
                ));
            }
        }
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryCountDTO getInventoryCount(UUID branchId) {
        List<BranchStock> stocks = branchStockRepo.findByBranchId(branchId);
        
        Set<UUID> productIds = stocks.stream().map(BranchStock::getProductId).collect(Collectors.toSet());
        Set<UUID> batchIds = stocks.stream().map(BranchStock::getBatchId).collect(Collectors.toSet());
        
        int totalQuantity = stocks.stream().mapToInt(BranchStock::getQuantity).sum();
        int lowStockItems = (int) stocks.stream().filter(BranchStock::isLowStock).count();
        
        LocalDate today = LocalDate.now();
        int expiringBatches = 0;
        int expiredBatches = 0;
        
        for (UUID batchId : batchIds) {
            Batch batch = batchRepo.findById(batchId).orElse(null);
            if (batch != null) {
                if (batch.isExpiredNow()) {
                    expiredBatches++;
                } else if (batch.isExpiringSoon()) {
                    expiringBatches++;
                }
            }
        }
        
        return new InventoryCountDTO(
                branchId,
                null, // branchName
                productIds.size(),
                batchIds.size(),
                totalQuantity,
                lowStockItems,
                expiringBatches,
                expiredBatches
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryCountDTO> getAllInventoryCounts() {
        List<BranchStock> allStocks = new ArrayList<>();
        List<Product> allProducts = productRepo.findAll();
        
        for (Product product : allProducts) {
            allStocks.addAll(branchStockRepo.findByProductId(product.getId()));
        }
        
        Map<UUID, List<BranchStock>> byBranch = allStocks.stream()
                .collect(Collectors.groupingBy(BranchStock::getBranchId));

        List<InventoryCountDTO> result = new ArrayList<>();
        
        for (Map.Entry<UUID, List<BranchStock>> entry : byBranch.entrySet()) {
            UUID branchId = entry.getKey();
            List<BranchStock> stocks = entry.getValue();
            
            Set<UUID> productIds = stocks.stream().map(BranchStock::getProductId).collect(Collectors.toSet());
            Set<UUID> batchIds = stocks.stream().map(BranchStock::getBatchId).collect(Collectors.toSet());
            
            int totalQuantity = stocks.stream().mapToInt(BranchStock::getQuantity).sum();
            int lowStockItems = (int) stocks.stream().filter(BranchStock::isLowStock).count();
            
            int expiringBatches = 0;
            int expiredBatches = 0;
            
            for (UUID batchId : batchIds) {
                Batch batch = batchRepo.findById(batchId).orElse(null);
                if (batch != null) {
                    if (batch.isExpiredNow()) {
                        expiredBatches++;
                    } else if (batch.isExpiringSoon()) {
                        expiringBatches++;
                    }
                }
            }
            
            result.add(new InventoryCountDTO(
                    branchId,
                    null,
                    productIds.size(),
                    batchIds.size(),
                    totalQuantity,
                    lowStockItems,
                    expiringBatches,
                    expiredBatches
            ));
        }
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovementReportDTO> getMovementReport(UUID branchId, LocalDateTime from, LocalDateTime to) {
        List<StockMovement> movements = movementRepo.findByBranchIdAndCreatedAtBetween(branchId, from, to);
        return movements.stream().map(this::toMovementReportDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovementReportDTO> getMovementReportByType(UUID branchId, MovementType type, LocalDateTime from, LocalDateTime to) {
        List<StockMovement> movements = movementRepo.findByBranchIdAndMovementTypeAndCreatedAtBetween(branchId, type, from, to);
        return movements.stream().map(this::toMovementReportDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovementReportDTO> getMovementReportByProduct(UUID productId, LocalDateTime from, LocalDateTime to) {
        List<StockMovement> movements = movementRepo.findByProductIdAndCreatedAtBetween(productId, from, to);
        return movements.stream().map(this::toMovementReportDTO).toList();
    }

    private MovementReportDTO toMovementReportDTO(StockMovement movement) {
        Product product = productRepo.findById(movement.getProductId()).orElse(null);
        Batch batch = batchRepo.findById(movement.getBatchId()).orElse(null);
        
        return new MovementReportDTO(
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
