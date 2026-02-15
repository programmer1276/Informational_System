package com.example.is_curse_work.dto;

import java.util.List;

public record ReportSummary(
        long expiredCount,
        long notificationCount,
        List<ZoneCountDto> topZones
) {}
