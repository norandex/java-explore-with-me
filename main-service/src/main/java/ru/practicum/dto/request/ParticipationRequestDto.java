package ru.practicum.dto.request;

import lombok.*;
import ru.practicum.model.enums.RequestStatus;


@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestDto {

    private Long id;

    private String created;

    private Long event;

    private Long requester;

    private RequestStatus status;
}
