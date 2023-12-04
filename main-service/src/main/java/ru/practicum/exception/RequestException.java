package ru.practicum.exception;

public class RequestException extends RuntimeException {
    public RequestException(String ex) {
        super(ex);
    }
}