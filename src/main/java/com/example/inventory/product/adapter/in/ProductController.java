package com.example.inventory.product.adapter.in;

import com.example.inventory.product.dto.ProductRequestDTO;
import com.example.inventory.product.dto.ProductResponseDTO;
import com.example.inventory.product.dto.ProductStatusUpdateDTO;
import com.example.inventory.product.dto.ProductUpdateDTO;
import com.example.inventory.product.port.in.ProductUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductUseCase useCase;

    public ProductController(ProductUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO dto) {
        ProductResponseDTO created = useCase.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> list() {
        return ResponseEntity.ok(useCase.list());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable UUID productId) {
        return ResponseEntity.ok(useCase.getById(productId));
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductResponseDTO> getBySku(@PathVariable String sku) {
        return ResponseEntity.ok(useCase.getBySku(sku));
    }

    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<ProductResponseDTO>> listByBrand(@PathVariable String brand) {
        return ResponseEntity.ok(useCase.listByBrand(brand));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponseDTO>> listByCategory(@PathVariable String category) {
        return ResponseEntity.ok(useCase.listByCategory(category));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(useCase.search(keyword));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable UUID productId,
            @Valid @RequestBody ProductUpdateDTO dto) {
        return ResponseEntity.ok(useCase.update(productId, dto));
    }

    @PatchMapping("/{productId}/status")
    public ResponseEntity<ProductResponseDTO> updateStatus(
            @PathVariable UUID productId,
            @Valid @RequestBody ProductStatusUpdateDTO dto) {
        return ResponseEntity.ok(useCase.updateStatus(productId, dto));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> delete(@PathVariable UUID productId) {
        useCase.delete(productId);
        return ResponseEntity.noContent().build();
    }
}
