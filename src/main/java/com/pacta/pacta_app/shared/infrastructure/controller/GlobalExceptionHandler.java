package com.pacta.pacta_app.shared.infrastructure.controller;

import com.pacta.pacta_app.shared.domain.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
class GlobalExceptionHandler {

    record ErrorResponse(String error) {}

    @ExceptionHandler(StorageException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ErrorResponse handleStorageException(StorageException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    /**
     * Domain precondition violations (invalid state transitions).
     * Rich domain objects throw IllegalStateException — map to 409 Conflict.
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorResponse handleDomainViolation(IllegalStateException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ErrorResponse handleException(Exception ex) {
        log.error("Unhandled exception", ex);
        return new ErrorResponse(String.format("An unexpected error occurred %s", ex.getMessage()));
    }
}
