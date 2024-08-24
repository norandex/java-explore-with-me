package ru.practicum.mapper;

import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.model.enums.EventState;

import java.time.LocalDateTime;

public class EventMapper {

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .eventDate(DateMapper.formatToString(event.getEventDate()))
                .annotation(event.getAnnotation())
                .paid(event.getPaid())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequestCount())
                .title(event.getTitle())
                .views(event.getViews())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .build();
    }

    public static EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .eventDate(DateMapper.formatToString(event.getEventDate()))
                .annotation(event.getAnnotation())
                .paid(event.getPaid())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequestCount())
                .createdOn(DateMapper.formatToString(event.getCreatedOn()))
                .title(event.getTitle())
                .description(event.getDescription())
                .state(event.getState())
                .views(event.getViews())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(event.getLocation())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(DateMapper.formatToString(event.getPublishedOn()))
                .requestModeration(event.getRequestModeration())
                .build();
    }

    public static Event toEvent(NewEventDto event,
                                Category category,
                                EventState eventState,
                                User initiator,
                                LocalDateTime localDateTime) {
        return Event.builder()
                .eventDate(DateMapper.formatToDateTime(event.getEventDate()))
                .annotation(event.getAnnotation())
                .paid(event.getPaid())
                .category(category)
                .createdOn(localDateTime)
                .title(event.getTitle())
                .description(event.getDescription())
                .state(eventState)
                .initiator(initiator)
                .location(event.getLocation())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration() != null ? event.getRequestModeration() : true)
                .build();
    }

}
