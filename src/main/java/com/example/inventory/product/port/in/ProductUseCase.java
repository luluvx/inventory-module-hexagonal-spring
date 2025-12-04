package com.example.inventory.product.port.in;

import com.example.inventory.product.dto.ProductRequestDTO;
import com.example.inventory.product.dto.ProductResponseDTO;
import com.example.inventory.product.dto.ProductStatusUpdateDTO;
import com.example.inventory.product.dto.ProductUpdateDTO;

import java.util.List;
import java.util.UUID;

public interface ProductUseCase {
    ProductResponseDTO create(ProductRequestDTO dto);
    ProductResponseDTO getById(UUID id);
    ProductResponseDTO getBySku(String sku);
    List<ProductResponseDTO> list();
    List<ProductResponseDTO> listByBrand(String brand);
    List<ProductResponseDTO> listByCategory(String category);
    List<ProductResponseDTO> search(String keyword);
    ProductResponseDTO update(UUID id, ProductUpdateDTO dto);
    ProductResponseDTO updateStatus(UUID id, ProductStatusUpdateDTO dto);
    void delete(UUID id);
}
