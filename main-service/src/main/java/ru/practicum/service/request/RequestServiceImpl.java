package ru.practicum.service.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.exception.*;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.model.enums.EventState;
import ru.practicum.model.enums.RequestStatus;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public ParticipationRequestDto create(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new DuplicateEntityException("Request already exists");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new EventStatusException("Event not published");
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new WrongInitiatorException("Initiator cannot send request to his own event");
        }
        if (event.getParticipantLimit() != 0 &&
                requestRepository.countByEventIdAndRequestStatusIs(eventId, RequestStatus.CONFIRMED) >= event.getParticipantLimit()) {
            throw new ExceededLimitException("Request limit has been exceeded");
        }
        Request request = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .requestStatus(RequestStatus.PENDING)
                .build();

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            if (event.getConfirmedRequestCount() == null) {
                event.setConfirmedRequestCount(1L);
            } else {
                event.setConfirmedRequestCount(event.getConfirmedRequestCount() + 1L);
            }
            request.setRequestStatus(RequestStatus.CONFIRMED);
        }
        requestRepository.save(request);
        return RequestMapper.toParticipationRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        List<Request> userRequests = requestRepository.findAllByRequesterId(userId);
        return userRequests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .toList();
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));
        if (request.getRequester().getId().equals(userId)) {
            request.setRequestStatus(RequestStatus.CANCELED);
        } else {
            throw new ForbiddenException("User with userId= "
                    + userId + " did not create request with ID= " + request.getEvent().getId());
        }
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new WrongRequestException("User is not the event initiator");
        }

        List<Request> requests = requestRepository.findAllByEventId(eventId);

        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .toList();
    }

    @Override
    public EventRequestStatusUpdateResult updateRequests(Long userId,
                                                         Long eventId,
                                                         EventRequestStatusUpdateRequest
                                                                 eventRequestStatusUpdateRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (Optional.ofNullable(eventRequestStatusUpdateRequest.getRequestIds()).isEmpty()) {
            throw new RequestStatusException("Field request id's shall not be blank");
        }
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            return new EventRequestStatusUpdateResult();
        }

        List<Request> requests = requestRepository
                .findAllByEventIdAndIdIn(eventId, eventRequestStatusUpdateRequest.getRequestIds()); //get all requests

        if (requests.stream().anyMatch(request -> !request.getRequestStatus().equals(RequestStatus.PENDING))) {
            throw new ForbiddenException("Only PENDING requests allowed");
        } // can update only PENDING requests
        if (requestRepository.countByEventIdAndRequestStatusIs(eventId, RequestStatus.CONFIRMED) >= event.getParticipantLimit()) {
            throw new WrongRequestException("The participant limit has been reached");
        }
        if (event.getConfirmedRequestCount() == null) {
            event.setConfirmedRequestCount(0L);
        }
        if (RequestStatus.CONFIRMED.equals(eventRequestStatusUpdateRequest.getStatus())) {
            requests.forEach(r -> {
                if (event.getConfirmedRequestCount() < event.getParticipantLimit()) {
                    r.setRequestStatus(RequestStatus.CONFIRMED);
                    event.setConfirmedRequestCount(event.getConfirmedRequestCount() + 1);
                } else {
                    r.setRequestStatus(RequestStatus.REJECTED);
                }
            });
        } else {
            requests.forEach(request -> request.setRequestStatus(eventRequestStatusUpdateRequest.getStatus()));
        }
        requestRepository.saveAll(requests);
        eventRepository.save(event);
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        requests.forEach(r -> {
            if (r.getRequestStatus().equals(RequestStatus.CONFIRMED)) {
                result.getConfirmedRequests().add(RequestMapper.toParticipationRequestDto(r));
            }
            if (r.getRequestStatus().equals(RequestStatus.REJECTED)) {
                result.getRejectedRequests().add(RequestMapper.toParticipationRequestDto(r));
            }
        });
        return result;
    }
}
