package com.example.is_curse_work.repository.function;

import com.example.is_curse_work.dto.ZoneMapDto;

import java.util.List;

public interface MapFunctionRepository {
    List<ZoneMapDto> getFridgeMap(Long fridgeId);
}


