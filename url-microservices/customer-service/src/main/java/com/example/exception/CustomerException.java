package com.example.exception;

import org.springframework.http.HttpStatus;

public class CustomerException extends RuntimeException{
    private HttpStatus httpStatus;

    public CustomerException(HttpStatus httpStatus, String message){
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
