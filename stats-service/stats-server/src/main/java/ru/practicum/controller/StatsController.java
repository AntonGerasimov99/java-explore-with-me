package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.StatisticRequestDto;
import ru.practicum.dto.StatisticResponseDto;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService service;

    @PostMapping(path = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@Valid @RequestBody StatisticRequestDto requestDto) {
        log.info("Received statistic information to save: {}", requestDto.toString());
        service.save(requestDto);
    }

    @GetMapping(path = "/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<StatisticResponseDto> getStats(@RequestParam String start,
                                               @RequestParam String end,
                                               @RequestParam(required = false) Optional<List<String>> uris,
                                               @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Received request to get stats");
        return service.getStatistic(start, end, uris, unique);
    }
}