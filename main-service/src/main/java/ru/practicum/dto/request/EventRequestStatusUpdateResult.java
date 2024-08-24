package ru.practicum.dto.request;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateResult {

    private List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();

    private List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
}
