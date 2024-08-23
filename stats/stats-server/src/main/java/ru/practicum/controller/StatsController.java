package ru.practicum.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.service.StatsService;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public HitDto postHit(@RequestBody HitDto hitDto) {
        log.info("post hit");
        return statsService.postHit(hitDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> getStats(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("get stats");
        return statsService.getStats(start, end, uris, unique);
    }
}