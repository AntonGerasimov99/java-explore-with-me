package ru.practicum.service;

import ru.practicum.dto.StatisticRequestDto;
import ru.practicum.dto.StatisticResponseDto;

import java.util.List;

public interface StatsService {

    void save(StatisticRequestDto requestDto);

    List<StatisticResponseDto> getStatistic(String start, String end, List<String> uris, boolean unique);
}