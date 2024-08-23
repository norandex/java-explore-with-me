package ru.practicum.service;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;

import java.util.List;

public interface StatsService {

    HitDto postHit(HitDto endpointHitRequestDto);

    List<StatsDto> getStats(String start, String end, List<String> uris, Boolean unique);
}
