package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.service.RequestService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping(path = "/users/{userId}/requests")
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createdRequest(@PathVariable long userId,
                                     @RequestParam(name = "eventId") @Positive long eventId) {
        log.info("Received request to create event with id: {}, user id: {}", eventId, userId);
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping(path = "/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public RequestDto cancelRequest(@PathVariable long userId,
                                    @PathVariable long requestId) {
        log.info("Received request to cancel request with id: {}, user id: {}", requestId, userId);
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getRequests(@PathVariable long userId) {
        log.info("Received request to get all requests by user id: {}", userId);
        return requestService.getRequests(userId);
    }
}