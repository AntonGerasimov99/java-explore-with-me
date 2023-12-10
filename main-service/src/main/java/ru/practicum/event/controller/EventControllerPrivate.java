package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping(path = "/users/{userId}/events")
public class EventControllerPrivate {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Receive request to create event by user with id {}", userId);
        return eventService.createEvent(userId, newEventDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(@PathVariable long userId,
                                         @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(name = "size", defaultValue = "10") @PositiveOrZero int size) {
        log.info("Receive request to get private events by param");
        return eventService.getEventsPrivate(userId, from, size);
    }

    @GetMapping(path = "/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEvent(@PathVariable long userId, @PathVariable long eventId) {
        log.info("Receive request to get private event with id {} by user with id {}", eventId, userId);
        return eventService.getEventPrivate(userId, eventId);
    }

    @GetMapping(path = "/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getEventRequest(@PathVariable long userId, @PathVariable long eventId) {
        log.info("Receive request to get private eventRequest for event with id {} by user with id {}", eventId, userId);
        return eventService.getEventRequestsPrivate(userId, eventId);
    }

    @PatchMapping(path = "/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable long userId, @PathVariable long eventId,
                                    @Valid @RequestBody UpdateEventRequestDto updateEventRequestDto) {
        log.info("Receive request to update event with id {} by user with id {}", eventId, userId);
        return eventService.updateEventPrivate(userId, eventId, updateEventRequestDto);
    }

    @PatchMapping(path = "/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateEventRequests(@PathVariable long userId, @PathVariable long eventId,
                                                              @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("Receive request to update eventRequest, event with id {} by user with id {}", eventId, userId);
        return eventService.updateEventRequestPrivate(userId, eventId, eventRequestStatusUpdateRequest);
    }
}