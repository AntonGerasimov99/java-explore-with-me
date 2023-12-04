package ru.practicum.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.request.dto.EventConfirmedRequests;
import ru.practicum.request.model.Request;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByIdIn(List<Long> ids);

    List<Request> findAllByEventIn(long eventId);

    Optional<Request> findByRequesterIdAndEventId(long requesterId, long eventId);

    EventConfirmedRequests getAmountOfConfirmedRequests(long eventId);

    Optional<Request> findByIdAndRequesterId(long requestId, long requesterId);

    List<Request> findAllByRequesterId(long requesterId);
}