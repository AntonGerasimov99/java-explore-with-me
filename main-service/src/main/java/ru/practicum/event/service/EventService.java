package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.request.dto.RequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    EventFullDto createEvent(long userId, NewEventDto newEventDto);

    EventFullDto updateEventPrivate(long userId, long eventId, UpdateEventRequestDto updateEventRequestDto);

    EventFullDto getEventPrivate(long userId, long eventId);

    List<EventShortDto> getEventsPrivate(long userId, int from, int size);

    EventRequestStatusUpdateResult updateEventRequestPrivate(long userId, long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<RequestDto> getEventRequestsPrivate(long userId, long eventId);

    EventFullDto getEventPublic(long eventId, HttpServletRequest request);

    List<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid, String rangeStart,
                                        String rangeEnd, Boolean available, String sort, int from, int size, HttpServletRequest request);

    List<EventFullDto> getEventsAdmin(List<Long> users, List<String> states, List<Long> categories, String rangeStart,
                                      String rangeEnd, int from, int size);

    EventFullDto updateEvenAdmin(long eventId, UpdateEventRequestDto updateEventRequestDto);
}