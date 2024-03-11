package com.example.urlsconvert.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomAuthenticationException.class)
    public ResponseEntity<UrlErrorResponse> handleAuthenticationException(CustomAuthenticationException e) {
        //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        UrlErrorResponse error = new UrlErrorResponse();
        error.setStatus(HttpStatus.UNAUTHORIZED.value());
        error.setMessage(e.getMessage());
        error.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<UrlErrorResponse> handleRegistrationException(RegistrationException e) {
        //return ResponseEntity.badRequest().body(e.getMessage());
        UrlErrorResponse error = new UrlErrorResponse();
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setMessage(e.getMessage());
        error.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidJwtTokenException.class)
    public ResponseEntity<UrlErrorResponse> handleInvalidJwtException(InvalidJwtTokenException e) {
        //return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        UrlErrorResponse error = new UrlErrorResponse();
        error.setStatus(HttpStatus.FORBIDDEN.value());
        error.setMessage(e.getMessage());
        error.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }
}
