package ru.practicum.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@Builder
public class NewCommentDto {

    private Long id;

    @NotBlank
    @Size(min = 1, max = 280)
    private String text;
}