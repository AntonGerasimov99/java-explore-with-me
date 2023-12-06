package ru.practicum.event.dto;

import java.util.Optional;

public enum PublicEventSort {
    EVENT_DATE,
    VIEWS;

    public static Optional<PublicEventSort> parseSort(String sort) {
        return Optional.of(PublicEventSort.valueOf(sort));
    }
}