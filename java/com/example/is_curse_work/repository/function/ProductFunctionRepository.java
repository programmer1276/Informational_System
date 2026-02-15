package com.example.is_curse_work.repository.function;

import com.example.is_curse_work.dto.UserProductDto;

import java.time.OffsetDateTime;
import java.util.List;

public interface ProductFunctionRepository {
    Long addProduct(Long ownerId, Long zoneId, Long categoryId, String name, String barcode, OffsetDateTime expiresAt, boolean locked);
    List<UserProductDto> getUserProducts(Long userId);

    void moveProduct(Long productId, Long toZoneId, Long actorId, String comment);
    void extendProduct(Long productId, OffsetDateTime newExpiresAt, Long actorId, String comment);
    void setStatus(Long productId, String status, Long actorId, String comment);
}

