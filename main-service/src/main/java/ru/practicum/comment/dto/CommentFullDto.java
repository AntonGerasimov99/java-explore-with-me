package ru.practicum.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class CommentFullDto {

    @NotBlank
    private Long id;

    @NotBlank
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @NotBlank
    private EventShortDto event;

    @NotBlank
    private UserShortDto author;

    private Boolean edited;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateEdited;

    @NotBlank
    private String text;
    private CommentShortDto replyComment;
}