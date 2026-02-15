package com.example.is_curse_work.dto;

import com.example.is_curse_work.model.Product;
import com.example.is_curse_work.model.ProductHistory;

import java.time.OffsetDateTime;
import java.util.List;

public record ProductDetailView(
        Long productId,
        String name,
        String status,
        String zoneName,
        OffsetDateTime expiresAt,
        boolean locked,
        List<ProductHistory> history
) {
    public static ProductDetailView from(Product p, List<ProductHistory> history) {
        return new ProductDetailView(
                p.getProductId(),
                p.getName(),
                p.getStatus(),
                p.getZone().getName(),
                p.getExpiresAt(),
                p.isLocked(),
                history
        );
    }
}

