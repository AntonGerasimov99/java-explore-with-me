package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentFullDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.service.CommentService;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping(path = "/comments/{userId}")
public class CommentControllerPrivate {

    private final CommentService commentService;

    @PostMapping(path = "/{userId}/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto createComment(@PathVariable long userId,
                                        @PathVariable long eventId,
                                        @RequestBody NewCommentDto newCommentDto) {
        log.info("Receive request to create comment");
        return commentService.createCommentPrivate(userId, eventId, newCommentDto);
    }

    @PostMapping(path = "/{userId}/{eventId}/{replyId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto createComment(@PathVariable long userId,
                                        @PathVariable long eventId,
                                        @PathVariable long replyId,
                                        @RequestBody NewCommentDto newCommentDto) {
        log.info("Receive request to create reply comment");
        return commentService.createCommentReplyPrivate(userId, eventId, replyId, newCommentDto);
    }

    @PatchMapping(path = "/{userId}/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentFullDto updateComment(@PathVariable long userId,
                                        @PathVariable long commentId,
                                        @RequestBody NewCommentDto newCommentDto) {
        log.info("Receive request to update comment with id {} by user with id {}", commentId, userId);
        return commentService.updateCommentPrivate(userId, commentId, newCommentDto);
    }

    @DeleteMapping(path = "/{userId}/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long userId,
                              @PathVariable long commentId) {
        log.info("Receive request to delete comment with id {} by user with id {}", commentId, userId);
        commentService.deleteCommentPrivate(userId, commentId);
    }
}