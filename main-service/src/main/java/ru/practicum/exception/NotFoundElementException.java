package ru.practicum.exception;

public class NotFoundElementException extends RuntimeException {
    public NotFoundElementException(String ex) {
        super(ex);
    }
}