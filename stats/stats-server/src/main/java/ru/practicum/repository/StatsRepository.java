package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query("SELECT h.app as app, h.uri as uri, count(h.ip) " +
            "FROM EndpointHit as h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "AND h.uri IN ?3 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY count(distinct(h.ip)) DESC")
    List<ViewStats> findAllByTimestampAndListOfUris(LocalDateTime startTime, LocalDateTime endTime, List<String> uris);

    @Query("SELECT h.app, h.uri, count(DISTINCT(h.ip)) " +
            "FROM EndpointHit as h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "AND h.uri IN ?3 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<ViewStats> findAllByTimestampAndListOfUrisAndUniqueIp(LocalDateTime startTime, LocalDateTime endTime, List<String> uris);

    @Query("SELECT h.app as app, h.uri as uri, count(h.ip) " +
            "FROM EndpointHit as h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY count(distinct(h.ip)) DESC ")
    List<ViewStats> findAllByTimestamp(LocalDateTime startTime, LocalDateTime endTime);

    @Query("SELECT h.app as app, h.uri as uri, count(DISTINCT(h.ip)) " +
            "FROM EndpointHit as h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY app, uri " +
            "ORDER BY COUNT(h.ip) DESC ")
    List<ViewStats> findAllByTimestampAndUniqueIp(LocalDateTime startTime, LocalDateTime endTime);
}
