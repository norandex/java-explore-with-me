package ru.practicum.controller.PrivateController;


import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.like.LikeDto;
import ru.practicum.dto.like.LikeProjection;
import ru.practicum.dto.like.UserRatingProjection;
import ru.practicum.model.enums.RatingSortFormat;
import ru.practicum.service.like.LikeService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/like")
@RequiredArgsConstructor
@Validated
public class PrivateLikeController {

    private final LikeService likeService;

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public LikeDto like(@PathVariable Long userId,
                        @PathVariable Long eventId,
                        @RequestParam(required = false, name = "like", defaultValue = "true") Boolean like) {
        return likeService.like(userId, eventId, like);
    }


    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId, @PathVariable Long eventId) {
        likeService.delete(userId, eventId);
    }

    @GetMapping("/authors")
    public List<UserRatingProjection> getAuthorsRating(@RequestParam(name = "sort", defaultValue = "DESCENDING") RatingSortFormat ratingSortFormat,
                                                       @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                                       @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        return likeService.getAuthorsRating(ratingSortFormat, from, size);
    }

    @GetMapping
    public List<LikeProjection> getUserLikes(@PathVariable Long userId,
                                             @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                             @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        return likeService.getUserLikes(userId, from, size);
    }

    @GetMapping("/authors/{authorId}")
    public UserRatingProjection getAuthorRating(@PathVariable Long authorId) {
        return likeService.getAuthorRating(authorId);
    }

}
