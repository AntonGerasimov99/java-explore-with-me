package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends ResponseStatusException {
    public ValidationException(String ex) {
        super(HttpStatus.BAD_REQUEST, ex);
    }
}
