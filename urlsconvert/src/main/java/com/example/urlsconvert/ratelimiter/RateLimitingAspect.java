package com.example.urlsconvert.ratelimiter;

import com.example.urlsconvert.utils.ClientIPUtils;
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
    private RateLimiterService rateLimiterService;
    @Autowired
    private HttpServletRequest request;

    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable{
        String ip = getClientIP();
        String key = "rate_limit:" + ip;

        boolean allowed = rateLimiterService.allowRequest(
                key,
                rateLimit.capacity(),
                rateLimit.refillTime(),
                rateLimit.refillAmount()
        );
        if(!allowed){
            throw new RateLimitExceededException("Too many requests");
        }
        return joinPoint.proceed();
    }


    private String getClientIP(){
        String key = ClientIPUtils.getClientIpAddress(request);
        //System.out.println(key);
        return key;
    }

//    private String getClientIP(){
//        String xfHeader = request.getHeader("X-Forwarded-For");
//        String ip = (xfHeader == null) ? request.getRemoteAddr() : xfHeader.split(",")[0];
//        return ip;
//    }

//    @Autowired
//    private HttpServletRequest request;
//
//    @Autowired
//    private RateLimiterService rateLimiterService;
//    @Pointcut("@annotation(com.example.urlsconvert.ratelimiter.RateLimited)")
//    public void rateLimit(){}
//    @Around("rateLimit()")
//    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable{
////        if (!rateLimiterService.allowRequest(request)){
//
////            throw new RateLimitExceededException("Rate limit exceeded");
////        }
//        return joinPoint.proceed();
//    }

}
