package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentFullDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.storage.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.storage.EventRepository;
import ru.practicum.exception.NotFoundElementException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.model.User;
import ru.practicum.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentFullDto createCommentPrivate(long userId, long eventId, NewCommentDto newCommentDto) {
        User author = findUser(userId);
        Event event = findEvent(eventId);
        if (event.getState().equals(EventState.PENDING)) {
            throw new ValidationException("Cant create comment for pending event");
        }
        Comment comment = CommentMapper.dtoToComment(newCommentDto, author, event);
        comment = commentRepository.save(comment);
        log.info("Comment saved with id {}", comment.getId());
        return CommentMapper.commentToFullDto(comment);
    }

    @Override
    @Transactional
    public CommentFullDto createCommentReplyPrivate(long userId, long eventId, long commentReplyId, NewCommentDto newCommentDto) {
        User author = findUser(userId);
        Comment mainComment = findComment(commentReplyId);
        Event event = findEvent(eventId);
        if (event.getState().equals(EventState.PENDING)) {
            throw new ValidationException("Cant create comment for pending event");
        }
        Comment comment = CommentMapper.dtoToComment(newCommentDto, author, event);
        comment.setReplyComment(mainComment);
        comment = commentRepository.save(comment);
        log.info("Reply comment saved with id {}", comment.getId());
        return CommentMapper.commentToFullDto(comment);
    }

    @Override
    @Transactional
    public CommentFullDto updateCommentPrivate(long userId, long commentId, NewCommentDto newCommentDto) {
        Comment commentForUpdate = findComment(commentId);
        User author = findUser(userId);
        checkCommentAndAuthor(author, commentForUpdate);
        commentForUpdate.setText(newCommentDto.getText());
        commentForUpdate.setEdited(true);
        commentForUpdate.setDateEdited(LocalDateTime.now());
        commentForUpdate = commentRepository.save(commentForUpdate);
        log.info("Comment with id {} updated", commentForUpdate.getId());
        return CommentMapper.commentToFullDto(commentForUpdate);
    }

    @Override
    @Transactional
    public void deleteCommentPrivate(long userId, long commentId) {
        Comment comment = findComment(commentId);
        User author = findUser(userId);
        checkCommentAndAuthor(author, comment);
        comment.setText("Comment deleted");
        comment.setEdited(true);
        comment.setDateEdited(LocalDateTime.now());
    }

    @Override
    @Transactional
    public List<CommentFullDto> getCommentsForEventPublic(long eventId) {
        Event event = findEvent(eventId);
        List<CommentFullDto> result = commentRepository.findAllByEventId(eventId).stream()
                .map(CommentMapper::commentToFullDto)
                .collect(Collectors.toList());
        log.info("Comments for event with id {} found, size = {}", eventId, result.size());
        return result;
    }

    @Override
    @Transactional
    public List<CommentFullDto> getCommentsForUserPublic(long userId) {
        User user = findUser(userId);
        List<CommentFullDto> result = commentRepository.findAllByAuthorId(userId).stream()
                .map(CommentMapper::commentToFullDto)
                .collect(Collectors.toList());
        log.info("Comments for user with id {} found, size = {}", user.getId(), result.size());
        return result;
    }

    @Override
    @Transactional
    public CommentFullDto getCommentPublic(long commentId) {
        Comment comment = findComment(commentId);
        log.info("Comment with id {} found", commentId);
        return CommentMapper.commentToFullDto(comment);
    }

    private User findUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundElementException("User with id " + userId + " not found"));
    }

    private Event findEvent(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundElementException("Event with id " + eventId + " not found"));
    }

    private Comment findComment(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundElementException("Comment with id " + commentId + " not found"));
    }

    private void checkCommentAndAuthor(User user, Comment comment) {
        if (!Objects.equals(user.getId(), comment.getAuthor().getId())) {
            throw new ValidationException("This user not author");
        }
    }
}