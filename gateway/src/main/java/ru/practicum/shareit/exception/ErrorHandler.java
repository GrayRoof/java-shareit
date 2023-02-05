package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;
import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({
            ValidationException.class,
            MethodArgumentNotValidException.class,
            MissingRequestHeaderException.class,
            MissingServletRequestParameterException.class
    })
    @ResponseBody
    public ResponseEntity<ErrorDto> handleValidationException(Throwable throwable) {
        return createErrorResponse(throwable);
    }

    @ExceptionHandler
    @ResponseBody
    public ResponseEntity<ErrorDto> handleOtherwise(Throwable throwable) {
        log.error("Unexpected error occurred:\n{}", ExceptionUtils.getStackTrace(throwable));
        return createErrorResponse("Ошибка: " + ExceptionUtils.getMessage(throwable),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorDto> createErrorResponse(Throwable throwable) {
        return createErrorResponse(throwable.getMessage() + Arrays.toString(throwable.getStackTrace()),
                HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorDto> createErrorResponse(String error, HttpStatus status) {
        return new ResponseEntity<>(new ErrorDto(error), status);
    }
}