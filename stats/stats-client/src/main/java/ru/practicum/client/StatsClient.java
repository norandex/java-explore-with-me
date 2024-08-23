package ru.practicum.client;

import ru.practicum.dto.StatsDto;

import java.util.List;

public interface StatsClient {
    void saveHit(String app, String uri, String ip, String timestamp);

    List<StatsDto> getStats(String start, String end, List<String> uris, Boolean unique);
}