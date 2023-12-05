package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.request.dto.EventConfirmedRequests;
import ru.practicum.request.dto.RequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    EventFullDto createEvent(long userId, NewEventDto newEventDto);

    EventFullDto updateEvent(long userId, long eventId, UpdateEventRequestDto updateEventRequestDto);

    EventFullDto getEvent(long userId, long eventId);

    List<EventShortDto> getEventsPublic(long userId, int from, int size);

    EventRequestStatusUpdateResult updateEventRequest(long userId, long eventId, EventConfirmedRequests eventConfirmedRequests);

    List<RequestDto> getEventRequests(long userId, long eventId);

    EventFullDto getEventPublic(long eventId, HttpServletRequest request);

    List<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid, String rangeStart,
                                        String rangeEnd, Boolean available, String sort, int from, int size, HttpServletRequest request);

    List<EventFullDto> getEventsAdmin(List<Long> users, List<String> states, List<Long> categories, String rangeStart,
                                      String rangeEnd, int from, int size);

    EventFullDto updateEvenAdmin(long eventId, UpdateEventRequestDto updateEventRequestDto);
}