package com.example.urlsconvert.ratelimiter;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
@Service
public class RateLimiterService {
    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final long capacity;
    private final long refillTokens;
    private final long refillPeriod;
    private final TimeUnit refillUnit;

//    public RateLimiterService(long capacity, long refillTokens, long refillPeriod, TimeUnit refillUnit){
//        this.capacity = capacity;
//        this.refillTokens = refillTokens;
//        this.refillPeriod = refillPeriod;
//        this.refillUnit = refillUnit;
//    }
    public RateLimiterService(){
        // max 100 tokens, 10 tokens added each minutes
        this.capacity = 10; //100
        this.refillTokens = 1; // 10
        this.refillPeriod = 1;
        this.refillUnit = TimeUnit.MINUTES;
    }

    public boolean allowRequest(HttpServletRequest request){
        String ip = request.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(ip, k -> new Bucket(capacity, refillTokens, refillPeriod, refillUnit));
        return bucket.consume();
    }

    private static class Bucket {
        private long capacity;
        private long tokens;
        private long lastRefillTimestamp;
        private final long refillTokens;
        private final long refillPeriodInMilli;

        Bucket(long capacity, long refillTokens, long refillPeriod, TimeUnit refillUnit) {
            this.capacity = capacity;
            this.tokens = capacity;
            this.refillTokens = refillTokens;
            this.refillPeriodInMilli = refillUnit.toMillis(refillPeriod);
            this.lastRefillTimestamp = System.currentTimeMillis();
        }

        synchronized  boolean consume(){
            refill();
            if (tokens > 0){
                tokens--;
                return true;
            } else {
                return false;
            }
        }

        private void refill(){
            long now = System.currentTimeMillis();
            long elapsedTime = now - lastRefillTimestamp;
            if(elapsedTime > refillPeriodInMilli){
                long tokensToAdd = (elapsedTime / refillPeriodInMilli) * refillTokens;
                tokens = Math.min(capacity, tokens + tokensToAdd);
                lastRefillTimestamp = now;
            }

        }
    }
}
