package ru.practicum.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.storage.CategoryRepository;
import ru.practicum.client.StatsClient;
import ru.practicum.comment.dto.CommentFullDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.storage.CommentRepository;
import ru.practicum.dto.StatisticResponseDto;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.Location;
import ru.practicum.event.storage.EventRepository;
import ru.practicum.event.storage.LocationRepository;
import ru.practicum.exception.NotFoundElementException;
import ru.practicum.exception.RequestException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.service.RequestService;
import ru.practicum.request.storage.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.storage.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final StatsClient statsClient;
    private final ObjectMapper objectMapper;
    private final RequestService requestService;
    private final CommentRepository commentRepository;
    private final DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public EventFullDto createEvent(long userId, NewEventDto newEventDto) {
        Event event = checkAndMappingEvent(userId, newEventDto);
        Event newEvent = eventRepository.save(event);
        log.info("New event saved with id: {}, timestamp {}", newEvent.getId(), newEvent.getCreatedOn());
        return EventMapper.eventToFullDto(newEvent, getCommentForEvent(newEvent.getId()));
    }

    @Override
    @Transactional
    public EventFullDto updateEventPrivate(long userId, long eventId, UpdateEventRequestDto updateEventRequestDto) {
        User initiator = findUser(userId);
        Event event = findEvent(eventId);
        if (!initiator.getId().equals(event.getInitiator().getId())) {
            throw new NotFoundElementException("User id " + initiator.getId() + "not initiator event with id" + event.getId());
        }
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new RequestException("Event already published");
        }
        event = updateEventFromRequest(event, updateEventRequestDto);
        event = eventRepository.save(event);
        log.info("Event with id: {} updated", event.getId());
        EventFullDto result = getViewsAndConfirmedRequestsForFullDto(event);
        return result;
    }

    @Override
    @Transactional
    public EventFullDto getEventPrivate(long userId, long eventId) {
        User user = findUser(userId);
        Optional<Event> event = eventRepository.findByInitiatorIdAndId(userId, eventId);
        if (event.isEmpty()) {
            throw new NotFoundElementException("Event with id " + eventId + "by user with id " + userId + "not found");
        }
        EventFullDto eventFullDto = getViewsAndConfirmedRequestsForFullDto(event.get());
        log.info("EventFullDto found by getEvent");
        return eventFullDto;
    }

    @Override
    @Transactional
    public List<EventShortDto> getEventsPrivate(long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        Page<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        List<EventShortDto> eventsDto = getViewsAndConfirmedRequestsForShortDto(events.toList());
        log.info("EventsShortDto found by getEvents");
        return eventsDto;
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateEventRequestPrivate(long userId, long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        User initiator = findUser(userId);
        Event event = findEvent(eventId);
        checkEventAndInitiator(event, initiator);
        List<Long> ids = eventRequestStatusUpdateRequest.getRequestIds();
        List<Request> requests = findRequestsAndCheck(eventRequestStatusUpdateRequest);
        Map<Long, Long> confirmedReq = requestService.getConfirmedRequests(List.of(event));
        long confirmedReqForEvent = confirmedReq.getOrDefault(event.getId(), 0L);
        if (confirmedReqForEvent == event.getParticipantLimit()) {
            throw new RequestException("Participant limit full");
        }
        return updateRequestsAndConvertToDto(requests, confirmedReqForEvent, event, eventRequestStatusUpdateRequest);
    }

    @Override
    @Transactional
    public List<RequestDto> getEventRequestsPrivate(long userId, long eventId) {
        User initiator = findUser(userId);
        Event event = findEvent(eventId);
        if (!event.getInitiator().getId().equals(initiator.getId())) {
            throw new NotFoundElementException("User can not update event request");
        }
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream()
                .map(RequestMapper::requestToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto getEventPublic(long eventId, HttpServletRequest request) {
        Event event = findEvent(eventId);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundElementException("Event with id " + eventId + "not published");
        }
        EventFullDto eventFullDto = getViewsAndConfirmedRequestsForFullDto(event);
        log.info("Event with id {} found", eventId);
        return eventFullDto;
    }

    @Override
    @Transactional
    public List<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid, String rangeStart,
                                               String rangeEnd, Boolean available, String sort, int from, int size, HttpServletRequest request) {
        checkStartAndEnd(rangeStart, rangeEnd);
        List<Event> events = publicParamFilter(text, categories, paid, rangeStart, rangeEnd, available, sort);
        List<EventShortDto> result = getViewsAndConfirmedRequestsForShortDto(events);
        PublicEventSort eventSort = PublicEventSort.parseSort(sort)
                .orElseThrow(() -> new NotFoundElementException("Sort not found"));
        result = result.subList(from, result.size());
        if (result.size() > size) {
            result = result.subList(0, size);
        }
        if (eventSort.equals(PublicEventSort.EVENT_DATE)) {
            return result.stream()
                    .sorted(Comparator.comparing(EventShortDto::getEventDate))
                    .collect(Collectors.toList());
        }
        return result.stream()
                .sorted(Comparator.comparing(EventShortDto::getViews))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<EventFullDto> getEventsAdmin(List<Long> users, List<String> states, List<Long> categories,
                                             String rangeStart, String rangeEnd, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        checkStartAndEnd(rangeStart, rangeEnd);
        List<Event> events = adminParamFilter(users, states, categories, rangeStart, rangeEnd);
        events = events.subList(from, events.size());
        if (events.size() > size) {
            events = events.subList(0, size);
        }
        return events.stream()
                .map(this::getViewsAndConfirmedRequestsForFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEvenAdmin(long eventId, UpdateEventRequestDto updateEventRequestDto) {
        Event event = findEvent(eventId);
        event = updateEventFromRequestAdmin(event, updateEventRequestDto);
        event = eventRepository.save(event);
        log.info("Event with id: {} updated", event.getId());
        return getViewsAndConfirmedRequestsForFullDto(event);
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

    private Category findCategory(long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundElementException("Request with id " + categoryId + " not found"));
    }

    private Location findLocationOrCreate(Location location) {
        Location locationFromRep = locationRepository.findByLatAndLon(location.getLat(), location.getLon());
        if (locationFromRep == null) {
            locationFromRep = locationRepository.save(location);
        }
        return locationFromRep;
    }

    private Event checkAndMappingEvent(long userId, NewEventDto newEventDto) {
        LocalDateTime eventDate = parseAndCheckEventDate(newEventDto);
        User initiator = findUser(userId);
        Category category = findCategory(newEventDto.getCategory());
        Location location = findLocationOrCreate(newEventDto.getLocation());
        log.info("DtoEvent convert to Event successful");
        return EventMapper.dtoToEvent(newEventDto, eventDate, initiator, category, location);
    }

    private LocalDateTime parseAndCheckEventDate(NewEventDto newEventDto) {
        LocalDateTime result = LocalDateTime.parse(newEventDto.getEventDate(), date);
        if (result.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Time of event cant be earlier than 2 hours");
        }
        return result;
    }

    private List<EventShortDto> getViewsAndConfirmedRequestsForShortDto(List<Event> events) {
        Map<Long, Long> views = getStats(events);
        Map<Long, Long> confirmedRequests = requestService.getConfirmedRequests(events);
        return events.stream()
                .map(EventMapper::eventToShortDto)
                .peek(s -> {
                    s.setViews(views.getOrDefault(s.getId(), 0L));
                    s.setConfirmedRequests(confirmedRequests.getOrDefault(s.getId(), 0L));
                })
                .collect(Collectors.toList());
    }

    private EventFullDto getViewsAndConfirmedRequestsForFullDto(Event event) {
        Map<Long, Long> views = getStats(List.of(event));
        Map<Long, Long> confirmedRequests = requestService.getConfirmedRequests(List.of(event));
        EventFullDto result = EventMapper.eventToFullDto(event, getCommentForEvent(event.getId()));
        result.setConfirmedRequests(confirmedRequests.getOrDefault(event.getId(), 0L));
        result.setViews(views.getOrDefault(event.getId(), 0L));
        return result;
    }

    private Map<Long, Long> getStats(List<Event> events) {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start;
        List<String> uris = new ArrayList<>();
        for (Event e : events) {
            if (e.getPublishedTime() != null && e.getPublishedTime().isBefore(start)) {
                start = e.getPublishedTime();
            }
            uris.add("/events/" + e.getId());
        }
        Map<Long, Long> result = new HashMap<>();
        ResponseEntity<Object> response = statsClient.getStats(start.format(date), end.format(date), uris, true);
        List<StatisticResponseDto> stats = objectMapper.convertValue(response.getBody(), new TypeReference<>() {
        });
        if (!stats.isEmpty()) {
            for (StatisticResponseDto stat : stats) {
                String[] event = stat.getUri().split("/");
                result.put(Long.parseLong(event[event.length - 1]), stat.getHits());
            }
        }
        return result;
    }

    private Event updateEventFromRequest(Event event, UpdateEventRequestDto updateEventRequestDto) {
        if (updateEventRequestDto.getEventDate() != null) {
            if (LocalDateTime.parse(updateEventRequestDto.getEventDate(), date).isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException("Time of event cant be earlier than 2 hours");
            }
            event.setEventDate(LocalDateTime.parse(updateEventRequestDto.getEventDate(), date));
        }
        if (updateEventRequestDto.getCategory() != null) {
            Category categoryFromRep = findCategory(updateEventRequestDto.getCategory());
            event.setCategory(categoryFromRep);
        }
        if (updateEventRequestDto.getAnnotation() != null && !updateEventRequestDto.getAnnotation().isBlank()) {
            checkAnnotation(updateEventRequestDto.getAnnotation());
            event.setAnnotation(updateEventRequestDto.getAnnotation());
        }
        if (updateEventRequestDto.getDescription() != null && !updateEventRequestDto.getDescription().isBlank()) {
            checkDescription(updateEventRequestDto.getDescription());
            event.setDescription(updateEventRequestDto.getDescription());
        }
        if (updateEventRequestDto.getTitle() != null && !updateEventRequestDto.getTitle().isBlank()) {
            checkTitle(updateEventRequestDto.getTitle());
            event.setTitle(updateEventRequestDto.getTitle());
        }
        if (updateEventRequestDto.getLocation() != null) {
            Location location = checkLocation(updateEventRequestDto.getLocation());
            event.setLocation(location);
        }
        if (updateEventRequestDto.getPaid() != null) {
            event.setPaid(updateEventRequestDto.getPaid());
        }
        if (updateEventRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventRequestDto.getParticipantLimit());
        }
        if (updateEventRequestDto.getRequestModeration() != null) {
            event.setIsRequestModeration(updateEventRequestDto.getRequestModeration());
        }
        if (updateEventRequestDto.getStateAction() != null) {
            if (event.getState().equals(EventState.PENDING) && updateEventRequestDto.getStateAction()
                    .equals(StateAction.CANCEL_REVIEW.toString())) {
                event.setState(EventState.CANCELED);
            } else if (event.getState().equals(EventState.CANCELED) && updateEventRequestDto.getStateAction()
                    .equals(StateAction.SEND_TO_REVIEW.toString())) {
                event.setState(EventState.PENDING);
            }
        }
        return event;
    }

    private Event updateEventFromRequestAdmin(Event event, UpdateEventRequestDto updateEventRequestDto) {
        if (updateEventRequestDto.getEventDate() != null) {
            if (LocalDateTime.parse(updateEventRequestDto.getEventDate(), date).isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ValidationException("Time of event cant be earlier than 1 hours");
            }
            event.setEventDate(LocalDateTime.parse(updateEventRequestDto.getEventDate(), date));
        }
        if (updateEventRequestDto.getStateAction() != null) {
            if (!event.getState().equals(EventState.PENDING)) {
                throw new RequestException("Incorrect EventState");
            }
            if (updateEventRequestDto.getStateAction().equals(StateAction.PUBLISH_EVENT.toString())) {
                event.setState(EventState.PUBLISHED);
            }
            if (updateEventRequestDto.getStateAction().equals(StateAction.REJECT_EVENT.toString())) {
                event.setState(EventState.CANCELED);
            }
        }
        if (updateEventRequestDto.getCategory() != null) {
            Category categoryFromRep = findCategory(updateEventRequestDto.getCategory());
            event.setCategory(categoryFromRep);
        }
        if (updateEventRequestDto.getAnnotation() != null && !updateEventRequestDto.getAnnotation().isBlank()) {
            checkAnnotation(updateEventRequestDto.getAnnotation());
            event.setAnnotation(updateEventRequestDto.getAnnotation());
        }
        if (updateEventRequestDto.getDescription() != null && !updateEventRequestDto.getDescription().isBlank()) {
            checkDescription(updateEventRequestDto.getDescription());
            event.setDescription(updateEventRequestDto.getDescription());
        }
        if (updateEventRequestDto.getTitle() != null && !updateEventRequestDto.getTitle().isBlank()) {
            checkTitle(updateEventRequestDto.getTitle());
            event.setTitle(updateEventRequestDto.getTitle());
        }
        if (updateEventRequestDto.getLocation() != null) {
            Location location = checkLocation(updateEventRequestDto.getLocation());
            event.setLocation(location);
        }
        if (updateEventRequestDto.getPaid() != null) {
            event.setPaid(updateEventRequestDto.getPaid());
        }
        if (updateEventRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventRequestDto.getParticipantLimit());
        }
        if (updateEventRequestDto.getRequestModeration() != null) {
            event.setIsRequestModeration(updateEventRequestDto.getRequestModeration());
        }
        return event;
    }

    private void checkAnnotation(String annotation) {
        if (annotation == null) {
            throw new ValidationException("Event annotation is null");
        }
        if (annotation.length() < 20 || annotation.length() > 2000) {
            throw new ValidationException("Event annotation can not be less than 20 and more than 2000 symbols");
        }
    }

    private void checkDescription(String description) {
        if (description == null) {
            throw new ValidationException("Event description is null");
        }
        if (description.length() < 20 || description.length() > 7000) {
            throw new ValidationException("Event description can not be less than 20 and more than 7000 symbols");
        }
    }

    private void checkTitle(String title) {
        if (title == null) {
            throw new ValidationException("Event annotation is null");
        }
        if (title.length() < 3 || title.length() > 120) {
            throw new ValidationException("Event title can not be less than 3 and more than 120 symbols");
        }
    }

    private Location checkLocation(Location location) {
        Location result = locationRepository.findByLatAndLon(location.getLat(), location.getLon());
        if (result == null) {
            result = locationRepository.save(location);
        }
        return result;
    }

    private void checkStartAndEnd(String rangeStart, String rangeEnd) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, date);
        }
        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, date);
        }
        if (start != null && end != null && start.isAfter(end)) {
            throw new ValidationException("Start can not be after end");
        }
    }

    private List<Event> adminParamFilter(List<Long> users, List<String> states, List<Long> categories,
                                         String rangeStart, String rangeEnd) {
        List<Event> events = eventRepository.findAll();
        if (users != null && !users.isEmpty()) {
            events = events.stream()
                    .filter(event -> users.contains(event.getInitiator().getId()))
                    .collect(Collectors.toList());
        }
        if (states != null) {
            List<EventState> eventStates = new ArrayList<>();
            for (String state : states) {
                eventStates.add(EventState.parseState(state));
            }
            events = events.stream()
                    .filter(event -> eventStates.contains(event.getState()))
                    .collect(Collectors.toList());
        }
        if (categories != null) {
            events = events.stream()
                    .filter(event -> categories.contains(event.getCategory().getId()))
                    .collect(Collectors.toList());
        }
        if (rangeStart != null) {
            LocalDateTime start = LocalDateTime.parse(rangeStart, date);
            events = events.stream()
                    .filter(event -> event.getEventDate().isAfter(start))
                    .collect(Collectors.toList());
        }
        if (rangeStart != null) {
            LocalDateTime end = LocalDateTime.parse(rangeEnd, date);
            events = events.stream()
                    .filter(event -> event.getEventDate().isBefore(end))
                    .collect(Collectors.toList());
        }
        return events;
    }

    private List<Event> publicParamFilter(String text, List<Long> categories, Boolean paid, String rangeStart,
                                          String rangeEnd, Boolean available, String sort) {
        List<Event> events = eventRepository.findAll();
        if (text != null) {
            events = events.stream()
                    .filter(event -> event.getDescription().toLowerCase().contains(text.toLowerCase())
                            || event.getAnnotation().toLowerCase().contains(text.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (categories != null) {
            events = events.stream()
                    .filter(event -> categories.contains(event.getCategory().getId()))
                    .collect(Collectors.toList());
        }
        if (paid != null) {
            events = events.stream()
                    .filter(event -> event.getPaid() == paid)
                    .collect(Collectors.toList());
        }
        if (available != null) {
            Map<Long, Long> confirmedRequests = requestService.getConfirmedRequests(events);
            events = events.stream()
                    .filter(event -> event.getParticipantLimit() == 0 || confirmedRequests.getOrDefault(event.getId(), 0L) < event.getParticipantLimit()) // todo проверить
                    .collect(Collectors.toList());
        }
        if (rangeStart != null) {
            LocalDateTime start = LocalDateTime.parse(rangeStart, date);
            events = events.stream()
                    .filter(event -> event.getEventDate().isAfter(start))
                    .collect(Collectors.toList());
        }
        if (rangeStart != null) {
            LocalDateTime end = LocalDateTime.parse(rangeEnd, date);
            events = events.stream()
                    .filter(event -> event.getEventDate().isBefore(end))
                    .collect(Collectors.toList());
        }
        return events;
    }

    private List<Request> findRequestsAndCheck(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        List<Long> ids = eventRequestStatusUpdateRequest.getRequestIds();
        List<Request> requests = requestRepository.findAllByIdIn(ids);
        for (Request request : requests) {
            if (!ids.contains(request.getId())) {
                throw new NotFoundElementException("Request with id " + request.getId() + " not found");
            }
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new RequestException("Request with id " + request.getId() + " dont have status PENDING");
            }
        }
        return requests;
    }

    private void checkEventAndInitiator(Event event, User initiator) {
        if (!event.getInitiator().getId().equals(initiator.getId())) {
            throw new NotFoundElementException("User with id " + initiator.getId() + " cant update event with id" + event.getId());
        }
        if (event.getParticipantLimit() == 0 || !event.getIsRequestModeration()) {
            throw new RequestException("Cant update, participantLimit = 0 or requestModeration = false");
        }
    }

    private EventRequestStatusUpdateResult updateRequestsAndConvertToDto(List<Request> requests, long confirmedReqForEvent,
                                                                         Event event, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        List<RequestDto> confirmedRequests = new ArrayList<>();
        List<RequestDto> rejectedRequests = new ArrayList<>();
        for (Request req : requests) {
            if (confirmedReqForEvent < event.getParticipantLimit()) {
                RequestStatus requestStatus = RequestStatus.parseStatus(eventRequestStatusUpdateRequest.getStatus())
                        .orElseThrow(() -> new NotFoundElementException("RequestStatus not found"));
                if (requestStatus.equals(RequestStatus.CONFIRMED)) {
                    req.setStatus(RequestStatus.CONFIRMED);
                    requestRepository.save(req);
                    confirmedRequests.add(RequestMapper.requestToDto(req));
                }
                if (requestStatus.equals(RequestStatus.REJECTED)) {
                    req.setStatus(RequestStatus.REJECTED);
                    requestRepository.save(req);
                    rejectedRequests.add(RequestMapper.requestToDto(req));
                }
            } else {
                req.setStatus(RequestStatus.REJECTED);
                requestRepository.save(req);
                rejectedRequests.add(RequestMapper.requestToDto(req));
            }
        }
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }

    private List<CommentFullDto> getCommentForEvent(Long eventId) {
        return commentRepository.findAllByEventId(eventId).stream()
                .map(CommentMapper::commentToFullDto)
                .collect(Collectors.toList());
    }
}