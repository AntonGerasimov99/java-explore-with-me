package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.storage.EventRepository;
import ru.practicum.exception.NotFoundElementException;
import ru.practicum.exception.RequestException;
import ru.practicum.request.dto.EventConfirmedRequests;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.storage.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public RequestDto createRequest(long userId, long eventId) {
        Request request = validRequest(userId, eventId);
        checkRequest(request, requestRepository.findByRequesterIdAndEventId(userId, eventId));
        if (!request.getEvent().getIsRequestModeration() || request.getEvent().getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }
        request = requestRepository.save(request);
        log.info("Request with id {} created", request.getId());
        return RequestMapper.requestToDto(request);
    }

    @Override
    @Transactional
    public RequestDto cancelRequest(long userId, long requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundElementException("Request with id " + requestId + " not found"));
        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.requestToDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public List<RequestDto> getRequests(long userId) {
        User user = findUser(userId);
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream()
                .map(RequestMapper::requestToDto)
                .collect(Collectors.toList());
    }


    private void checkRequest(Request request, Optional<Request> optionalRequest) {
        if (optionalRequest.isPresent()) {
            throw new RequestException("User with id" + request.getRequester().getId() + " already send request for event with id:" + request.getEvent().getId());
        }
        if (request.getEvent().getInitiator().getId() == request.getRequester().getId()) {
            throw new RequestException(("Owner of event cant send request"));
        }
        EventConfirmedRequests amountConfirmedRequests = requestRepository.getAmountOfConfirmedRequests(request.getEvent().getId());
        long count = (amountConfirmedRequests == null) ? 0 : amountConfirmedRequests.getAmountConfirmedRequests();
        if (request.getEvent().getParticipantLimit() != 0 && count == request.getEvent().getParticipantLimit()) {
            throw new RequestException("Participant limit exceeded");
        }
        if (!request.getEvent().getState().equals(EventState.PUBLISHED)) {
            throw new RequestException("Event with id " + request.getEvent().getId() + " is not published");
        }
    }

    private Request validRequest(long requesterId, long eventId) {
        User requester = findUser(requesterId);
        Event event = findEvent(eventId);
        return RequestMapper.createRequest(event, requester);
    }

    private User findUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundElementException("User with id " + userId + " not found"));
    }

    private Event findEvent(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundElementException("Event with id " + eventId + " not found"));
    }

    private Request findRequest(long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundElementException("Request with id " + requestId + " not found"));
    }
}