package com.example.is_curse_work.repository.function;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationFunctionRepositoryImpl implements NotificationFunctionRepository {

    private final JdbcTemplate jdbc;

    public NotificationFunctionRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Long createNotification(Long userId, Long productId, String channel, String template, String status, String errorMsg) {
        return jdbc.queryForObject(
                "select fn_create_notification(?, ?, ?::channel_enum, ?::template_enum, ?::notif_status_enum, ?)",
                Long.class,
                userId, productId, channel, template, status, errorMsg
        );
    }
}

