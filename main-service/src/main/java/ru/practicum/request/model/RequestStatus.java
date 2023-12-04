package ru.practicum.request.model;

import java.util.Optional;

public enum RequestStatus {

    PENDING,
    CONFIRMED,
    REJECTED,
    CANCELED;

    public static Optional<RequestStatus> parseStatus(String status) {
        return Optional.of(RequestStatus.valueOf(status));
    }
}
