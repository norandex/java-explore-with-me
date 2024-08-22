package ru.practicum.mapper;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.time.format.DateTimeFormatter;

public class StatsMapper {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static StatsDto toStatDto(ViewStats stat) {
        return StatsDto.builder()
                .app(stat.getApp())
                .uri(stat.getUri())
                .hits(stat.getHits())
                .build();
    }

    public static EndpointHit toHit(HitDto hitDto) {
        return EndpointHit.builder()
                .app(hitDto.getApp())
                .uri(hitDto.getUri())
                .ip(hitDto.getIp())
                .timestamp(hitDto.getTimestamp())
                .build();
    }

    public static HitDto toHitDto(EndpointHit hit) {
        return HitDto.builder()
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .timestamp(hit.getTimestamp())
                .build();
    }

}
