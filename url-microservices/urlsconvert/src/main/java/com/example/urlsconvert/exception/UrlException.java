package com.example.urlsconvert.exception;

import org.springframework.http.HttpStatus;

public class UrlException extends RuntimeException{
    private HttpStatus httpStatus;

    public UrlException(HttpStatus httpStatus, String message){
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus(){
        return this.httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus){
        this.httpStatus = httpStatus;
    }
}