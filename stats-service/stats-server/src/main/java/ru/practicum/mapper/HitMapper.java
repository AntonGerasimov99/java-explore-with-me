package ru.practicum.mapper;

import ru.practicum.dto.StatisticRequestDto;
import ru.practicum.model.App;
import ru.practicum.model.Hit;

public class HitMapper {

    public static Hit dtoToHit(StatisticRequestDto requestDto, App app) {
        return Hit.builder()
                .ip(requestDto.getIp())
                .uri(requestDto.getUri())
                .app(app)
                .timestamp(requestDto.getTimestamp())
                .build();
    }
}