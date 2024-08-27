package ru.practicum.controller.publicController;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.like.EventRatingProjection;
import ru.practicum.model.enums.RatingSortFormat;
import ru.practicum.service.like.LikeService;

import java.util.List;

@RestController
@RequestMapping("/events/rating")
@RequiredArgsConstructor
public class PublicLikeController {

    private final LikeService likeService;

    @GetMapping
    public List<EventRatingProjection> getPopularEvents(@RequestParam(name = "sort", defaultValue = "DESCENDING") RatingSortFormat ratingSortFormat,
                                                        @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                                        @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        return likeService.getPopularEvents(ratingSortFormat, from, size);
    }

    @GetMapping("/{eventId}")
    public EventRatingProjection getEventRating(@PathVariable Long eventId) {
        return likeService.getEventRating(eventId);
    }
}
