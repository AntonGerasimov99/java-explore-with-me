package ru.practicum.comment.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.comment.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByEventId(long eventId);

    List<Comment> findAllByAuthorId(long authorId);

    List<Comment> findAllByAuthorIdAndEventId(long authorId, long eventId);
}