package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError errorNotFound(NotFoundElementException e) {
        List<String> errors = getErrors(e);
        return convertExceptionToApi(e, errors);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError errorBadRequest(ValidationException e) {
        List<String> errors = getErrors(e);
        return convertExceptionToApi(e, errors);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError errorConflict(RequestException e) {
        List<String> errors = getErrors(e);
        return convertExceptionToApi(e, errors);
    }

    private List<String> getErrors(Exception e) {
        List<String> errors = new ArrayList<>();
        for (StackTraceElement stack : e.getStackTrace()) {
            errors.add(stack.toString());
        }
        return errors;
    }

    private ApiError convertExceptionToApi(Exception e, List<String> errors) {
        return ApiError.builder()
                .errors(errors)
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}