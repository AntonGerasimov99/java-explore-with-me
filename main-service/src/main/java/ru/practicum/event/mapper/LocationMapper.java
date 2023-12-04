package ru.practicum.event.mapper;

import ru.practicum.event.dto.LocationDto;
import ru.practicum.event.model.Location;

public class LocationMapper {

    public static LocationDto locationToDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}