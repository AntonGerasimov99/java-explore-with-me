package ru.practicum.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.request.dto.EventConfirmedRequests;
import ru.practicum.request.model.Request;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByIdIn(List<Long> ids);

    List<Request> findAllByEventId(long eventId);

    Optional<Request> findByRequesterIdAndEventId(long requesterId, long eventId);

    Optional<Request> findByIdAndRequesterId(long requestId, long requesterId);

    List<Request> findAllByRequesterId(long requesterId);

    @Query("select new ru.practicum.request.dto.EventConfirmedRequests(r.event.id, count(r.id)) " +
            "from Request as r " +
            "where r.event.id in :eventIds and r.status = 'CONFIRMED' " +
            "group by r.event.id")
    List<EventConfirmedRequests> findConfirmedRequestsByEventsId(List<Long> eventIds);

    @Query("select new ru.practicum.request.dto.EventConfirmedRequests(r.event.id, count(r.id)) " +
            "from Request as r " +
            "where r.event.id = :eventIds and r.status = 'CONFIRMED' " +
            "group by r.event.id")
    EventConfirmedRequests findConfirmedRequestsByEventId(long eventId);
}