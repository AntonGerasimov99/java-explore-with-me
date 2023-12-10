package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundElementException extends ResponseStatusException {
    public NotFoundElementException(String ex) {
        super(HttpStatus.NOT_FOUND, ex);
    }
}