package ru.practicum.service.event;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.dto.event.*;
import ru.practicum.model.enums.EventState;
import ru.practicum.model.enums.SortFormat;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    //public
    List<EventShortDto> getPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd, Boolean onlyAvailable, SortFormat sort, Integer from,
                                  Integer size, HttpServletRequest request);

    EventFullDto getByIdPublic(Long id, HttpServletRequest request);

    //Admin

    List<EventFullDto> getAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    //private
    EventFullDto create(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    EventFullDto getByIdUser(Long userId, Long eventId);


}
