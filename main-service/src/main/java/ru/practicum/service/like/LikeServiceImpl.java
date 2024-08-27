package ru.practicum.service.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.like.EventRatingProjection;
import ru.practicum.dto.like.LikeDto;
import ru.practicum.dto.like.LikeProjection;
import ru.practicum.dto.like.UserRatingProjection;
import ru.practicum.exception.EventStatusException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.LikeMapper;
import ru.practicum.model.Event;
import ru.practicum.model.Like;
import ru.practicum.model.LikeId;
import ru.practicum.model.User;
import ru.practicum.model.enums.EventState;
import ru.practicum.model.enums.RatingSortFormat;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.LikeRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final LikeRepository likeRepository;

    @Override
    @Transactional
    public LikeDto like(Long userId, Long eventId, Boolean isLike) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new EventStatusException("Ставить лайки можно только опубликованным событиям");
        }


        LikeId likeId = LikeId.builder()
                .event(event)
                .user(user)
                .build();

        Like like = Like.builder()
                .isLike(isLike)
                .id(likeId)
                .createdOn(LocalDateTime.now())
                .build();

        return LikeMapper.toLikeDto(likeRepository.save(like));
    }

    @Override
    public List<LikeProjection> getUserLikes(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Sort sort = Sort.by(Sort.Direction.DESC, "created_on");
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size, sort);

        return likeRepository.findAllByUserId(userId, page);

    }

    @Override
    @Transactional
    public void delete(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        LikeId likeId = LikeId.builder()
                .event(event)
                .user(user)
                .build();
        likeRepository.deleteById(likeId);

    }

    @Override
    public UserRatingProjection getAuthorRating(Long authorId) {
        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("User with id=" + authorId + " was not found"));

        return likeRepository.findAuthorRating(authorId);
    }

    @Override
    public List<UserRatingProjection> getAuthorsRating(RatingSortFormat ratingSortFormat, Integer from, Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "Rating");
        if (RatingSortFormat.ASCENDING.equals(ratingSortFormat)) {
            sort = Sort.by(Sort.Direction.ASC, "Rating");
        }
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size, sort);

        return likeRepository.findAuthorsRating(page);
    }

    @Override
    public List<EventRatingProjection> getPopularEvents(RatingSortFormat ratingSortFormat, Integer from, Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "Rating");
        if (RatingSortFormat.ASCENDING.equals(ratingSortFormat)) {
            sort = Sort.by(Sort.Direction.ASC, "Rating");
        }
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size, sort);

        return likeRepository.findEventsRating(page);
    }

    @Override
    public EventRatingProjection getEventRating(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        return likeRepository.findEventRating(eventId);
    }

}
