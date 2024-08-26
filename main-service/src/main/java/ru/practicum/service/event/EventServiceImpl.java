package ru.practicum.service.event;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.StatsDto;
import ru.practicum.dto.event.*;
import ru.practicum.exception.EventDateException;
import ru.practicum.exception.EventStatusException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.DateMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.model.enums.*;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final RequestRepository requestRepository;

    private final CategoryRepository categoryRepository;

    private final StatsClient statsClient;

    private final EntityManager entityManager;


    @Override
    public List<EventShortDto> getPublic(String text, List<Long> categoriesIds, Boolean paid, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Boolean onlyAvailable, SortFormat sort, Integer from,
                                         Integer size, HttpServletRequest request) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> eventRoot = criteriaQuery.from(Event.class);
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(rangeStart) && Objects.nonNull(rangeEnd) && rangeStart.isAfter(rangeEnd)) {
            throw new DateTimeException("Date time exception");
        }

        if (Objects.isNull(rangeStart)) {
            rangeStart = LocalDateTime.now();
        }

        if (Objects.nonNull(text)) {
            Predicate predicateAnnotation = criteriaBuilder.like(
                    criteriaBuilder.upper(
                            eventRoot.get("annotation")), "%" + text.toUpperCase() + "%");
            Predicate predicateDescription = criteriaBuilder.like(
                    criteriaBuilder.upper(
                            eventRoot.get("description")), "%" + text.toUpperCase() + "%");
            predicates.add(criteriaBuilder.or(predicateAnnotation, predicateDescription));
        }

        if (Objects.nonNull(categoriesIds) && !categoriesIds.isEmpty()) {
            List<Category> categories = categoryRepository.findAllByIdIn(categoriesIds);
            predicates.add(eventRoot.get("category").in(categories));
        }

        if (Objects.nonNull(paid)) {
            Predicate predicatePaid;
            if (paid) {
                predicatePaid = criteriaBuilder.isTrue(eventRoot.get("paid"));
            } else {
                predicatePaid = criteriaBuilder.isFalse(eventRoot.get("paid"));
            }
            predicates.add(predicatePaid);
        }

        Predicate predicateRangeStart = criteriaBuilder.greaterThanOrEqualTo(eventRoot.get("eventDate"), rangeStart);
        predicates.add(predicateRangeStart);

        if (Objects.nonNull(rangeEnd)) {
            Predicate predicateRangeEnd = criteriaBuilder.lessThanOrEqualTo(eventRoot.get("eventDate"), rangeEnd);
            predicates.add(predicateRangeEnd);
        }

        criteriaQuery.select(eventRoot).where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                .orderBy(criteriaBuilder.asc(eventRoot.get("eventDate")));

        List<Event> events = entityManager.createQuery(criteriaQuery)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();

        if (onlyAvailable) {
            events = events.stream()
                    .filter((event -> event.getConfirmedRequestCount() < (long) event.getParticipantLimit()))
                    .toList();
        }

        if (Objects.nonNull(sort)) {
            if (sort.equals(SortFormat.EVENT_DATE)) {
                events = events.stream().sorted(Comparator.comparing(Event::getEventDate)).toList();
            } else {
                events = events.stream().sorted(Comparator.comparing(Event::getViews)).toList();
            }
        }

        if (events.isEmpty()) {
            return new ArrayList<>();
        }

        saveHit(request);

        Map<Long, Long> views = getViews(events);

        Map<Long, Long> requests = getConfirmedRequests(events);

        return events.stream()
                .map(EventMapper::toEventShortDto)
                .peek(event -> event.setViews(views.getOrDefault(event.getId(), 0L)))
                .peek(event -> event.setConfirmedRequests(requests.getOrDefault(event.getId(), 0L)))
                .toList();
    }

    @Override
    public EventFullDto getByIdPublic(Long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", id)));
        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException(String.format("Event with id=%d was not found", id));
        }
        Map<Long, Long> views = getViews(List.of(event));

        Map<Long, Long> requests = getConfirmedRequests(List.of(event));

        EventFullDto result = EventMapper.toEventFullDto(event);
        result.setConfirmedRequests(requests.getOrDefault(event.getId(), 0L));
        result.setViews(views.getOrDefault(event.getId(), 0L));

        saveHit(request);

        return result;
    }

    @Override
    public List<EventFullDto> getAdmin(List<Long> usersIds, List<EventState> states, List<Long> categoriesIds,
                                       LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        log.info("get admin event service");
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> eventRoot = criteriaQuery.from(Event.class);
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(categoriesIds) && !categoriesIds.isEmpty()) {
            List<Category> categories = categoryRepository.findAllByIdIn(categoriesIds);
            predicates.add(eventRoot.get("category").in(categories));
        }

        if (Objects.nonNull(usersIds) && !usersIds.isEmpty()) {
            List<User> users = userRepository.findAllByIdIn(usersIds);
            predicates.add(eventRoot.get("initiator").in(users));
        }

        if (Objects.nonNull(rangeStart)) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    eventRoot.get("eventDate").as(LocalDateTime.class), rangeStart));
        }
        if (Objects.nonNull(rangeEnd)) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    eventRoot.get("eventDate").as(LocalDateTime.class), rangeEnd));
        }

        criteriaQuery.select(eventRoot).where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
        List<Event> events = entityManager.createQuery(criteriaQuery)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();

        if (events.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, Long> views = getViews(events);

        Map<Long, Long> requests = getConfirmedRequests(events);

        events = events.stream()
                .peek(event -> event.setConfirmedRequestCount(requests.getOrDefault(event.getId(), 0L)))
                .peek(event -> event.setViews(views.getOrDefault(event.getId(), 0L)))
                .toList();
        return events.stream()
                .map(EventMapper::toEventFullDto)
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto updateAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        LocalDateTime updateEventDate = DateMapper.formatToDateTime(updateEventAdminRequest.getEventDate());
        if (Objects.nonNull(updateEventDate) && LocalDateTime.now().isAfter(updateEventDate.minusHours(1))) {
            throw new EventDateException("Cannot publish the event because it doesn't meet date time requirements");
        }

        if (LocalDateTime.now().isAfter(event.getEventDate().minusHours(1))) {
            throw new EventDateException("Cannot publish the event because it doesn't meet date time requirements");
        }
        if (Objects.nonNull(updateEventAdminRequest.getAnnotation())) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (Objects.nonNull(updateEventAdminRequest.getCategory())) {
            Category category = categoryRepository.findById(updateEventAdminRequest.getCategory()).orElseThrow(() ->
                    new NotFoundException("Category with id= " + updateEventAdminRequest.getCategory() + " was not found"));
            event.setCategory(category);
        }

        if (Objects.nonNull(updateEventAdminRequest.getDescription())) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }

        if (Objects.nonNull(updateEventAdminRequest.getLocation())) {
            event.setLocation(updateEventAdminRequest.getLocation());
        }

        if (Objects.nonNull(updateEventAdminRequest.getPaid())) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }

        if (Objects.nonNull(updateEventAdminRequest.getParticipantLimit())) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }

        if (Objects.nonNull(updateEventAdminRequest.getRequestModeration())) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }

        if (Objects.nonNull(updateEventAdminRequest.getTitle())) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        if (Objects.nonNull(updateEventAdminRequest.getStateAction())) {
            AdminStateAction stateAction = updateEventAdminRequest.getStateAction();
            if (stateAction.equals(AdminStateAction.PUBLISH_EVENT)) {
                if (Objects.nonNull(event.getPublishedOn()) || event.getState().equals(EventState.CANCELED)) {
                    throw new EventStatusException("Cannot publish the event because " +
                            "it's not in the right state: " + event.getState());
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (stateAction.equals(AdminStateAction.REJECT_EVENT)) {
                if (Objects.nonNull(event.getPublishedOn())) {
                    throw new EventStatusException("Cannot publish the event because it's not in " +
                            "the right state: " + event.getState());
                }
                event.setState(EventState.CANCELED);
            }
        }
        if (Objects.nonNull(updateEventDate)) {
            if (Objects.nonNull(event.getPublishedOn())) {
                if (updateEventDate.isBefore(LocalDateTime.now())
                        || updateEventDate.isBefore(event.getPublishedOn().plusHours(1))) {
                    throw new DateTimeException("The start date of the event to be modified" +
                            " is less than one hour from the publication date.");
                }
            }
            event.setEventDate(updateEventDate);
        }
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto create(Long userId, NewEventDto newEventDto) {
        LocalDateTime newEventDate = DateMapper.formatToDateTime(newEventDto.getEventDate());

        if (Objects.nonNull(newEventDate) && LocalDateTime.now().isAfter(newEventDate.minusHours(2L))) {
            throw new DateTimeException("Field: eventDate. Error: должно содержать дату, которая еще не наступила." +
                    " Value:" + newEventDate);
        }

        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() ->
                new NotFoundException("Category with id= " + newEventDto.getCategory() + " was not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Event event = EventMapper.toEvent(newEventDto, category, EventState.PENDING, user, LocalDateTime.now());
        if (Objects.isNull(event.getPaid())) {
            event.setPaid(Boolean.FALSE);
        }
        if (Objects.isNull(event.getParticipantLimit())) {
            event.setParticipantLimit(0L);
        }
        log.info(event.toString());
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageRequest).toList();
        return events.stream().map(EventMapper::toEventShortDto).toList();
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (Optional.ofNullable(updateEventUserRequest).isEmpty()) {
            return EventMapper.toEventFullDto(event);
        }
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new EventStatusException("Only pending or canceled events can be changed");
        }

        if (Optional.ofNullable(updateEventUserRequest.getEventDate()).isPresent()) {
            LocalDateTime eventDate = DateMapper.formatToDateTime(updateEventUserRequest.getEventDate());
            if (Objects.nonNull(eventDate) && LocalDateTime.now().isAfter(eventDate.minusHours(2))) {
                throw new EventDateException("Event date exception");
            }
            event.setEventDate(DateMapper.formatToDateTime(updateEventUserRequest.getEventDate()));
        }
        if (Objects.nonNull(updateEventUserRequest.getAnnotation())) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (Objects.nonNull(updateEventUserRequest.getCategory())) {
            Long categoryId = updateEventUserRequest.getCategory();
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " was not found"));
            event.setCategory(category);
        }
        if (Objects.nonNull(updateEventUserRequest.getLocation())) {
            event.setLocation(updateEventUserRequest.getLocation());
        }

        if (Objects.nonNull(updateEventUserRequest.getDescription())) {
            event.setDescription(updateEventUserRequest.getDescription());
        }

        if (Objects.nonNull(updateEventUserRequest.getParticipantLimit())) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }

        if (Objects.nonNull(updateEventUserRequest.getPaid())) {
            event.setPaid(updateEventUserRequest.getPaid());
        }

        if (Objects.nonNull(updateEventUserRequest.getRequestModeration())) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (Objects.nonNull(updateEventUserRequest.getTitle()) && updateEventUserRequest.getTitle().length() >= 3) {
            event.setTitle(updateEventUserRequest.getTitle());
        }
        if (Objects.nonNull(updateEventUserRequest.getStateAction())) {
            if (updateEventUserRequest.getStateAction().equals(UserStateAction.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            } else {
                event.setState(EventState.CANCELED);
            }
        }
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getByIdUser(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        return EventMapper.toEventFullDto(event);
    }

    private Map<Long, Long> getConfirmedRequests(List<Event> events) {
        List<Request> requests = requestRepository.findAllByEventInAndRequestStatusIs(events, RequestStatus.CONFIRMED);
        Map<Long, Long> result = new HashMap<>();
        for (Event event : events) {
            for (Request request : requests) {
                if (request.getEvent().getId().equals(event.getId())) {
                    result.put(event.getId(), request.getId());
                }
            }
        }
        return result;
    }

    private void saveHit(HttpServletRequest request) {
        statsClient.saveHit("erm-service", request.getRequestURI(),
                request.getRemoteAddr(), DateMapper.formatToString(LocalDateTime.now()));
    }

    private Map<Long, Long> getViews(List<Event> events) {
        LocalDateTime start = events.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .orElseThrow(() -> new NotFoundException("Unknown Error"));

        List<String> uris = events.stream()
                .map(event -> String.format("/events/%s", event.getId()))
                .toList();

        List<StatsDto> views = statsClient.getStats(DateMapper.formatToString(start),
                DateMapper.formatToString(LocalDateTime.now()),
                uris, true);

        Map<Long, Long> eventViews = new HashMap<>();

        for (StatsDto view : views) {
            if (view.getUri().equals("/events")) {
                continue;
            }
            Long eventId = Long.parseLong(view.getUri().substring("/events".length() + 1));
            eventViews.put(eventId, view.getHits());
        }

        return eventViews;
    }
}
