package com.example.is_curse_work.dto;

import java.time.OffsetDateTime;

public record UserProductDto(
        Long productId,
        String name,
        OffsetDateTime expiresAt,
        String status,
        Long zoneId,
        Long fridgeId
) {}

