package ru.practicum.comment.mapper;

import ru.practicum.comment.dto.CommentFullDto;
import ru.practicum.comment.dto.CommentShortDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

public class CommentMapper {

    public static CommentFullDto commentToFullDto(Comment comment) {
        CommentFullDto result = CommentFullDto.builder()
                .id(comment.getId())
                .created(comment.getCreated())
                .event(EventMapper.eventToShortDto(comment.getEvent()))
                .author(UserMapper.userToShortDto(comment.getAuthor()))
                .edited(comment.getEdited())
                .dateEdited(comment.getDateEdited())
                .text(comment.getText())
                .build();
        if (comment.getReplyComment() != null) {
            result.setReplyComment(commentToShorDto(comment.getReplyComment()));
        }
        return result;
    }

    public static CommentShortDto commentToShorDto(Comment comment) {
        return CommentShortDto.builder()
                .id(comment.getId())
                .created(comment.getCreated())
                .author(UserMapper.userToShortDto(comment.getAuthor()))
                .text(comment.getText())
                .build();
    }

    public static Comment dtoToComment(NewCommentDto newCommentDto, User author, Event event) {
        return Comment.builder()
                .author(author)
                .event(event)
                .edited(false)
                .text(newCommentDto.getText())
                .build();
    }
}