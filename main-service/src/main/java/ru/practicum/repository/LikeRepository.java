package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.like.EventRatingProjection;
import ru.practicum.dto.like.LikeProjection;
import ru.practicum.dto.like.UserRatingProjection;
import ru.practicum.model.Like;
import ru.practicum.model.LikeId;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, LikeId> {

    @Query(value = "SELECT u.name as UserName, u.email as UserEmail, k.likeAmount as Rating " +
            "FROM (SELECT e.INITIATOR_ID, " +
            "SUM(CASE WHEN l.IS_LIKE IS TRUE THEN 1 WHEN l.IS_LIKE IS FALSE THEN -1 ELSE 0 END) AS likeAmount " +
            "FROM LIKES as l " +
            "RIGHT JOIN EVENTS e ON e.ID = l.EVENT_ID " +
            "GROUP BY e.INITIATOR_ID) AS k " +
            "LEFT JOIN USERS as u ON u.id = k.initiator_id", nativeQuery = true)
    List<UserRatingProjection> findAuthorsRating(Pageable pageable);


    @Query(value = "SELECT u.name as UserName, u.email as UserEmail, k.likeAmount as Rating " +
            "FROM (SELECT e.INITIATOR_ID, " +
            "SUM(CASE WHEN l.IS_LIKE IS TRUE THEN 1 WHEN l.IS_LIKE IS FALSE THEN -1 ELSE 0 END) AS likeAmount " +
            "FROM LIKES as l " +
            "RIGHT JOIN EVENTS e ON e.ID = l.EVENT_ID " +
            "GROUP BY e.INITIATOR_ID) AS k " +
            "LEFT JOIN USERS as u ON u.id = k.INITIATOR_ID " +
            "WHERE u.id = ?1", nativeQuery = true)
    UserRatingProjection findAuthorRating(Long userId);

    @Query(value = "SELECT k.description as Description, k.title as Title, k.likeAmount as Rating " +
            "FROM (SELECT e.description, e.title, " +
            "SUM(CASE WHEN l.IS_LIKE IS TRUE THEN 1 WHEN l.IS_LIKE IS FALSE THEN -1 ELSE 0 END) as likeAmount " +
            "FROM LIKES as l " +
            "RIGHT JOIN EVENTS as e ON e.id = l.event_id " +
            "GROUP BY e.id) as k", nativeQuery = true)
    List<EventRatingProjection> findEventsRating(Pageable pageable);

    @Query(value = "SELECT e.description as Description, e.title as Title, " +
            "SUM(CASE WHEN l.is_like IS TRUE THEN 1 WHEN l.is_like IS FALSE THEN -1 ELSE 0 END) as Rating " +
            "FROM LIKES as l " +
            "RIGHT JOIN EVENTS as e ON e.ID = l.event_id " +
            "GROUP BY e.id " +
            "HAVING e.ID = ?1", nativeQuery = true)
    EventRatingProjection findEventRating(Long eventId);

    @Query(value = "SELECT k.name as UserName, k.title as Title, k.is_like as IsLike, k.created_on as CreatedOn " +
            "FROM (SELECT u.name, e.title, l.is_like, l.created_on  " +
            "from likes l " +
            "LEFT JOIN USERS AS u ON u.id = l.user_id " +
            "LEFT JOIN EVENTS e ON e.id = l.event_id " +
            "WHERE u.id = ?1) AS k", nativeQuery = true)
    List<LikeProjection> findAllByUserId(Long userId, Pageable pageable);


}
