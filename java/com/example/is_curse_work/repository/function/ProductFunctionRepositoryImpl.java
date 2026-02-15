package com.example.is_curse_work.repository.function;

import com.example.is_curse_work.dto.UserProductDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public class ProductFunctionRepositoryImpl implements ProductFunctionRepository {

    private final JdbcTemplate jdbc;

    public ProductFunctionRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Long addProduct(Long ownerId, Long zoneId, Long categoryId, String name, String barcode, OffsetDateTime expiresAt, boolean locked) {
        return jdbc.queryForObject(
                "select fn_add_product(?, ?, ?, ?, ?, ?, null, ?, 'ACTIVE')",
                Long.class,
                ownerId, zoneId, categoryId, name, barcode, expiresAt, locked
        );
    }

    @Override
    public List<UserProductDto> getUserProducts(Long userId) {
        return jdbc.query(
                "select * from fn_get_user_products(?)",
                (ResultSet rs, int rowNum) -> new UserProductDto(
                        rs.getLong("product_id"),
                        rs.getString("name"),
                        rs.getObject("expires_at", OffsetDateTime.class),
                        rs.getString("status"),
                        rs.getLong("zone_id"),
                        rs.getLong("fridge_id")
                ),
                userId
        );
    }

    @Override
    public void moveProduct(Long productId, Long toZoneId, Long actorId, String comment) {
        jdbc.update("select fn_move_product(?, ?, ?, ?)", productId, toZoneId, actorId, comment);
    }

    @Override
    public void extendProduct(Long productId, OffsetDateTime newExpiresAt, Long actorId, String comment) {
        jdbc.update("select fn_extend_product(?, ?, ?, ?)", productId, newExpiresAt, actorId, comment);
    }

    @Override
    public void setStatus(Long productId, String status, Long actorId, String comment) {
        jdbc.update("select fn_set_product_status(?, ?::status_enum, ?, ?)", productId, status, actorId, comment);
    }
}

