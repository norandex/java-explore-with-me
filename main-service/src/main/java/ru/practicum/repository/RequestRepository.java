package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.enums.RequestStatus;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByEventIdAndIdIn(Long eventId, List<Long> requestIds);

    Boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    List<Request> findAllByRequesterId(Long userId);

    List<Request> findAllByEventId(Long eventId);

    Long countByEventIdAndRequestStatusIs(Long eventId, RequestStatus requestStatus);

    List<Request> findAllByEventInAndRequestStatusIs(List<Event> events, RequestStatus requestStatus);
}
