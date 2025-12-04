package com.example.inventory.product.application;

import com.example.inventory.product.application.mapper.ProductMapper;
import com.example.inventory.product.domain.Product;
import com.example.inventory.product.dto.ProductRequestDTO;
import com.example.inventory.product.dto.ProductResponseDTO;
import com.example.inventory.product.dto.ProductStatusUpdateDTO;
import com.example.inventory.product.dto.ProductUpdateDTO;
import com.example.inventory.product.port.in.ProductUseCase;
import com.example.inventory.product.port.out.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService implements ProductUseCase {

    private final ProductRepository repo;
    private final ProductMapper mapper;

    public ProductService(ProductRepository repo, ProductMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ProductResponseDTO create(ProductRequestDTO dto) {
        if (repo.existsBySku(dto.sku())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El SKU ya existe: " + dto.sku());
        }

        Product product = new Product(
                null,
                dto.name(),
                dto.description(),
                dto.sku().toUpperCase(),
                dto.brand(),
                dto.category(),
                dto.unitPrice(),
                dto.unit(),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        Product saved = repo.save(product);
        return mapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getById(UUID id) {
        Product product = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado con id: " + id));
        return mapper.toResponseDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getBySku(String sku) {
        Product product = repo.findBySku(sku.toUpperCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado con SKU: " + sku));
        return mapper.toResponseDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> list() {
        return repo.findAll().stream().map(mapper::toResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> listByBrand(String brand) {
        return repo.findByBrand(brand).stream().map(mapper::toResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> listByCategory(String category) {
        return repo.findByCategory(category).stream().map(mapper::toResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> search(String keyword) {
        return repo.search(keyword).stream().map(mapper::toResponseDTO).toList();
    }

    @Override
    @Transactional
    public ProductResponseDTO update(UUID id, ProductUpdateDTO dto) {
        Product current = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado con id: " + id));

        if (dto.sku() != null && !dto.sku().isBlank()) {
            String newSku = dto.sku().toUpperCase();
            if (!newSku.equals(current.getSku()) && repo.existsBySkuAndIdNot(newSku, id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El SKU ya existe: " + newSku);
            }
            current.setSku(newSku);
        }

        if (dto.name() != null) current.setName(dto.name());
        if (dto.description() != null) current.setDescription(dto.description());
        if (dto.brand() != null) current.setBrand(dto.brand());
        if (dto.category() != null) current.setCategory(dto.category());
        if (dto.unitPrice() != null) current.setUnitPrice(dto.unitPrice());
        if (dto.unit() != null) current.setUnit(dto.unit());
        current.setUpdatedAt(LocalDateTime.now());

        Product saved = repo.save(current);
        return mapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public ProductResponseDTO updateStatus(UUID id, ProductStatusUpdateDTO dto) {
        Product current = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado con id: " + id));
        
        current.setActive(dto.active());
        current.setUpdatedAt(LocalDateTime.now());
        
        Product saved = repo.save(current);
        return mapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado con id: " + id);
        }
        repo.deleteById(id);
    }
}
