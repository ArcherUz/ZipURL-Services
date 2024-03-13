package com.example.urlsconvert.ratelimiter;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RateLimitingAspect {
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private RateLimiterService rateLimiterService;
    @Pointcut("@annotation(com.example.urlsconvert.ratelimiter.RateLimited)")
    public void rateLimit(){}

    @Around("rateLimit()")
    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable{
        if (!rateLimiterService.allowRequest(request)){
            throw new RateLimitExceededException("Rate limit exceeded");
        }
        return joinPoint.proceed();
    }

}
