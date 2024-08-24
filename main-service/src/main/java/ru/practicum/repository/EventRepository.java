package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Event;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByIdIn(List<Long> eventIds);

    Page<Event> findAllByInitiatorId(Long userId, PageRequest pageRequest);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    Boolean existsByCategoryId(Long catId);

}
