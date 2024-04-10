package com.example.urlsconvert.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({UrlException.class})
    public ResponseEntity<Object> handleUrlException(UrlException e){
        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
    }
}
