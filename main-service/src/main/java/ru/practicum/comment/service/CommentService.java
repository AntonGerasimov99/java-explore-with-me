package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentFullDto;
import ru.practicum.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentService {

    CommentFullDto createCommentPrivate(long userId, long eventId, NewCommentDto newCommentDto);

    CommentFullDto createCommentReplyPrivate(long userId, long eventId, long commentReplyId, NewCommentDto newCommentDto);

    CommentFullDto updateCommentPrivate(long userId, long commentId, NewCommentDto newCommentDto);

    void deleteCommentPrivate(long userId, long commentId);

    List<CommentFullDto> getCommentsForEventPublic(long eventId);

    List<CommentFullDto> getCommentsForUserPublic(long userId);

    CommentFullDto getCommentPublic(long commentId);
}
