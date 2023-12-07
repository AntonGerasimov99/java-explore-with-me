package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping(path = "/compilations")
public class CompilationController {

    private final CompilationService compilationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(required = false) @PositiveOrZero int from,
                                                @RequestParam(required = false) @Positive int size) {
        log.info("Receive request to get public compilations");
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping(path = "/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getCompilation(@PathVariable long compilationId) {
        log.info("Receive request to get public compilation with id {}", compilationId);
        return compilationService.getCompilation(compilationId);
    }
}