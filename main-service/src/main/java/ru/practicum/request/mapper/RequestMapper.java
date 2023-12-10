package ru.practicum.request.mapper;

import ru.practicum.event.model.Event;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.model.Request;
import ru.practicum.user.model.User;

import java.time.format.DateTimeFormatter;

public class RequestMapper {

    public static RequestDto requestToDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .created(request.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")))
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus().name())
                .build();
    }

    public static Request createRequest(Event event, User requester) {
        return Request.builder()
                .event(event)
                .requester(requester)
                .build();
    }
}