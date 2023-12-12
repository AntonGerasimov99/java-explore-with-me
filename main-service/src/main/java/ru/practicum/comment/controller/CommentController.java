package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentFullDto;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping(path = "/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping(path = "/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentFullDto getComment(@PathVariable long commentId) {
        log.info("Receive request to get comment with id {}", commentId);
        return commentService.getCommentPublic(commentId);
    }

    @GetMapping(path = "/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentFullDto> getCommentByUserId(@PathVariable long userId) {
        log.info("Receive request to get all comments for user with id {}", userId);
        return commentService.getCommentsForUserPublic(userId);
    }

    @GetMapping(path = "/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentFullDto> getCommentByEventId(@PathVariable long eventId) {
        log.info("Receive request to get all comments for event with id {}", eventId);
        return commentService.getCommentsForEventPublic(eventId);
    }
}