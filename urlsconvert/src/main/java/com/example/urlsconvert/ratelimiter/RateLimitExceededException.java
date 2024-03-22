package com.example.urlsconvert.ratelimiter;

public class RateLimitExceededException extends RuntimeException{
    public RateLimitExceededException(String message){
        super(message);
    }
}
