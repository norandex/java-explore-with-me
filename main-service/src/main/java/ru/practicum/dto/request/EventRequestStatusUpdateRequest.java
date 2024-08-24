package ru.practicum.dto.request;

import lombok.*;
import ru.practicum.model.enums.RequestStatus;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;

    private RequestStatus status;
}
