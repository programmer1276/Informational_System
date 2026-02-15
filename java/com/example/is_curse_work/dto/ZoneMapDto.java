package com.example.is_curse_work.dto;

public record ZoneMapDto(
        Long zoneId,
        String zoneName,
        Integer capacityUnits,
        Integer sortOrder,
        Long activeCount,
        Long expiredCount
) {}

