package com.matheusvsdev.apipaymentauthentication.controllers.handlers;

import com.matheusvsdev.apipaymentauthentication.dto.CustomError;
import com.matheusvsdev.apipaymentauthentication.dto.ValidationError;
import com.matheusvsdev.apipaymentauthentication.exceptions.DuplicateWalletException;
import com.matheusvsdev.apipaymentauthentication.exceptions.InvalidTransactionException;
import com.matheusvsdev.apipaymentauthentication.exceptions.MaxWalletsLimitException;
import com.matheusvsdev.apipaymentauthentication.exceptions.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ControllerExceptions {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomError> methodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ValidationError error = new ValidationError(Instant.now(),
                status.value(),
                "Dados inválidos",
                request.getRequestURI());

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            error.addError(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(DuplicateWalletException.class)
    public ResponseEntity<CustomError> duplicateWallet(DuplicateWalletException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        CustomError error = new CustomError(Instant.now(),
                status.value(),
                e.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MaxWalletsLimitException.class)
    public ResponseEntity<CustomError> maxWalletsLimit(MaxWalletsLimitException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        CustomError error = new CustomError(Instant.now(),
                status.value(),
                e.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CustomError> notFound(NotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        CustomError error = new CustomError(Instant.now(),
                status.value(),
                e.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<CustomError> invalidTransaction(InvalidTransactionException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        CustomError error = new CustomError(Instant.now(),
                status.value(),
                e.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }
}
