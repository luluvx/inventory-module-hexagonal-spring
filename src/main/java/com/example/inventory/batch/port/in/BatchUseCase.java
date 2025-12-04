package com.example.inventory.batch.port.in;

import com.example.inventory.batch.dto.*;

import java.util.List;
import java.util.UUID;

public interface BatchUseCase {
    BatchResponseDTO create(BatchRequestDTO dto);
    BatchResponseDTO getById(UUID id);
    List<BatchResponseDTO> list();
    List<BatchResponseDTO> listByProduct(UUID productId);
    List<BatchResponseDTO> listExpiringSoon();
    List<BatchResponseDTO> listExpired();
    List<ExpiringBatchNotificationDTO> getExpiringNotifications();
    BatchResponseDTO update(UUID id, BatchUpdateDTO dto);
    BatchResponseDTO toggleNotification(UUID id, BatchNotificationDTO dto);
    void deactivateExpiredBatches();
    void delete(UUID id);
}
