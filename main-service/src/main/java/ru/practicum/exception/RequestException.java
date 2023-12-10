package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.CONFLICT)
public class RequestException extends ResponseStatusException {
    public RequestException(String ex) {
        super(HttpStatus.CONFLICT, ex);
    }
}