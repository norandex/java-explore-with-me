package ru.practicum.service.like;

import ru.practicum.dto.like.EventRatingProjection;
import ru.practicum.dto.like.LikeDto;
import ru.practicum.dto.like.LikeProjection;
import ru.practicum.dto.like.UserRatingProjection;
import ru.practicum.model.enums.RatingSortFormat;

import java.util.List;

public interface LikeService {

    LikeDto like(Long userId, Long eventId, Boolean like);

    List<LikeProjection> getUserLikes(Long userId, Integer from, Integer size);

    void delete(Long userId, Long eventId);

    EventRatingProjection getEventRating(Long eventId);

    UserRatingProjection getAuthorRating(Long userId);

    List<UserRatingProjection> getAuthorsRating(RatingSortFormat ratingSortFormat, Integer from, Integer size);

    List<EventRatingProjection> getPopularEvents(RatingSortFormat ratingSortFormat, Integer from, Integer size);

}
