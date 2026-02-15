package com.example.is_curse_work.repository.function;

import com.example.is_curse_work.dto.ZoneMapDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;

@Repository
public class MapFunctionRepositoryImpl implements MapFunctionRepository {

    private final JdbcTemplate jdbc;

    public MapFunctionRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<ZoneMapDto> getFridgeMap(Long fridgeId) {
        return jdbc.query(
                "select * from fn_get_fridge_map(?)",
                (ResultSet rs, int rowNum) -> new ZoneMapDto(
                        rs.getLong("zone_id"),
                        rs.getString("zone_name"),
                        rs.getInt("capacity_units"),
                        rs.getInt("sort_order"),
                        rs.getLong("active_count"),
                        rs.getLong("expired_count")
                ),
                fridgeId
        );
    }
}

