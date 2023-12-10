package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class NewCompilationDto {

    private Set<Long> events;
    private Boolean pinned;
    @NotBlank
    @NotNull
    @Size(min = 1, max = 50)
    private String title;
}