package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.mapper.DateMapper;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public HitDto postHit(HitDto hitDto) {
        EndpointHit hit = StatsMapper.toHit(hitDto);
        log.info(hit.toString());
        statsRepository.save(hit);
        return StatsMapper.toHitDto(hit);
    }

    @Override
    public List<StatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        log.info("get stats");
        LocalDateTime startTime = DateMapper.formatToDateTime(start);
        LocalDateTime endTime = DateMapper.formatToDateTime(end);
        List<ViewStats> result;
        if (uris != null) {
            if (unique) {
                result = statsRepository.findAllByTimestampAndListOfUrisAndUniqueIp(startTime, endTime, uris);
            } else {
                result = statsRepository.findAllByTimestampAndListOfUris(startTime, endTime, uris);
            }
        } else if (unique) {
            result = statsRepository.findAllByTimestampAndUniqueIp(startTime, endTime);
        } else {
            result = statsRepository.findAllByTimestamp(startTime, endTime);
        }

        return result.stream()
                .map(StatsMapper::toStatDto)
                .collect(Collectors.toList());
    }
}
