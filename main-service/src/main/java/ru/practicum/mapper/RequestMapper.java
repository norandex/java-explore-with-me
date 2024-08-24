package ru.practicum.mapper;

import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.model.Request;

public class RequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .requester(request.getRequester().getId())
                .event(request.getEvent().getId())
                .created(DateMapper.formatToString(request.getCreated()))
                .status(request.getRequestStatus())
                .build();
    }
}
