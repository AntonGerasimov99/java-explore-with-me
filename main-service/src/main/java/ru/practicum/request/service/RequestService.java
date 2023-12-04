package ru.practicum.request.service;

import ru.practicum.request.dto.RequestDto;

import java.util.List;

public interface RequestService {

    RequestDto createRequest(long userId, long eventId);

    RequestDto cancelRequest(long userId, long requestId);

    List<RequestDto> getRequests(long userId);
}