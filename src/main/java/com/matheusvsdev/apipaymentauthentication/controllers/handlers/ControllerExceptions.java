package com.matheusvsdev.apipaymentauthentication.controllers.handlers;

import com.matheusvsdev.apipaymentauthentication.dto.CustomError;
import com.matheusvsdev.apipaymentauthentication.exceptions.DuplicateWalletException;
import com.matheusvsdev.apipaymentauthentication.exceptions.MaxWalletsLimitException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ControllerExceptions {

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
}
