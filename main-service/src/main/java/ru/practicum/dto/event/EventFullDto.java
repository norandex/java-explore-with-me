package ru.practicum.dto.event;

import lombok.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.Location;
import ru.practicum.model.enums.EventState;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {

    private String annotation;

    private CategoryDto category;

    private Long confirmedRequests;

    private String createdOn;

    private String description;

    private String eventDate;

    private Long id;

    private UserShortDto initiator;

    private Location location;

    private Boolean paid;

    private Long participantLimit;

    private String publishedOn;

    private Boolean requestModeration;

    private EventState state;

    private String title;

    private Long views;
}