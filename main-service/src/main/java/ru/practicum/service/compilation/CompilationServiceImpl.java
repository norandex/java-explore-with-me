package ru.practicum.service.compilation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final EventRepository eventRepository;

    private final CompilationRepository compilationRepository;

    private final EntityManager entityManager;

    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        List<Event> events;
        if (newCompilationDto.getEvents() != null) {
            events = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
        } else {
            events = new ArrayList<>();
        }
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto, new HashSet<>(events));
        if (Optional.ofNullable(newCompilationDto.getPinned()).isEmpty()) {
            compilation.setPinned(Boolean.FALSE);
        }
        compilation = compilationRepository.save(compilation);
        List<EventShortDto> compilationEvents = compilation
                .getEvents()
                .stream()
                .map(EventMapper::toEventShortDto)
                .toList();
        return CompilationMapper.toCompilationDto(compilation, compilationEvents);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Compilation actual = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + id + " was not found"));
        compilationRepository.delete(actual);
    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));

        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }

        if (updateCompilationRequest.getEvents() != null) {
            List<Event> events = eventRepository.findAllByIdIn(updateCompilationRequest.getEvents());
            compilation.setEvents(new HashSet<>(events));
        }

        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }

        compilation = compilationRepository.save(compilation);
        List<EventShortDto> eventShortDtos = compilation
                .getEvents()
                .stream()
                .map(EventMapper::toEventShortDto)
                .toList();
        return CompilationMapper.toCompilationDto(compilation, eventShortDtos);
    }

    @Override
    public CompilationDto getById(Long id) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + id + " was not found"));
        List<EventShortDto> compilationEvents = compilation
                .getEvents()
                .stream()
                .map(EventMapper::toEventShortDto)
                .toList();
        return CompilationMapper.toCompilationDto(compilation, compilationEvents);
    }

    @Override
    public List<CompilationDto> get(Boolean pinned, Integer from, Integer size) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Compilation> criteriaQuery = criteriaBuilder.createQuery(Compilation.class);

        Root<Compilation> rootCompilation = criteriaQuery.from(Compilation.class);
        Predicate criteria = criteriaBuilder.conjunction();

        if (pinned != null) {
            Predicate isPinned;
            if (pinned) {
                isPinned = criteriaBuilder.isTrue(rootCompilation.get("pinned"));
            } else {
                isPinned = criteriaBuilder.isFalse(rootCompilation.get("pinned"));
            }
            criteria = criteriaBuilder.and(criteria, isPinned);
        }

        criteriaQuery.select(rootCompilation).where(criteria);
        List<Compilation> compilations = entityManager.createQuery(criteriaQuery)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
        return compilations.stream()
                .map(o -> CompilationMapper.toCompilationDto(o, o.getEvents()
                        .stream()
                        .map(EventMapper::toEventShortDto)
                        .toList())).toList();
    }

}
