package com.example.is_curse_work.repository.function;

public interface NotificationFunctionRepository {
    Long createNotification(Long userId, Long productId, String channel, String template, String status, String errorMsg);
}

