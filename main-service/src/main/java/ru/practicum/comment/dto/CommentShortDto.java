package ru.practicum.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class CommentShortDto {

    @NotBlank
    private Long id;

    @NotBlank
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @NotBlank
    private UserShortDto author;

    @NotBlank
    private String text;
}