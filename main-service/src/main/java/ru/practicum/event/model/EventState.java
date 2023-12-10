package ru.practicum.event.model;

public enum EventState {
    PENDING,
    PUBLISHED,
    CANCELED;

    public static EventState parseState(String s) {
        return EventState.valueOf(s);
    }
}
