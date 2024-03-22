package com.example.urlsconvert.ratelimiter;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
@Service
public class RateLimiterService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    private String tokenBucketScript;
    @PostConstruct
    public void init(){
        this.tokenBucketScript = loadScript("token_bucket.lua");
    }
    private String loadScript(String path){
        Resource resource = new ClassPathResource(path);
        try (InputStream is = resource.getInputStream()){
            System.out.println("Successfully loaded Lua script: "+ path);
            return StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        } catch (IOException e){
            throw new IllegalStateException("Failed to load Lua script from path: " + path, e);
        }
    }

    public boolean allowRequest(String ip, int capacity, int refillTime, int refillAmount){
        long currentTime = System.currentTimeMillis() / 1000;
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();


        redisScript.setScriptText(tokenBucketScript);
        redisScript.setResultType(Long.class);

        List<String> params = Arrays.asList(
                String.valueOf(capacity),
                String.valueOf(refillTime),
                String.valueOf(refillAmount),
                String.valueOf(currentTime)
        );

        // Log parameter types
        System.out.println("Executing Redis script with parameters:");
        System.out.println("IP: " + ip);
        params.forEach(param -> System.out.println("Parameter: " + param + " Type: " + param.getClass().getSimpleName()));

//        Long result = redisTemplate.execute(redisScript, Collections.singletonList(ip),
//                String.valueOf(capacity), String.valueOf(refillTime),
//                String.valueOf(refillAmount), String.valueOf(currentTime));
//
//        return result != null && result == 1;
        try {
            System.out.println("Executing Redis script with parameters:");
            System.out.println("IP: " + ip);
            System.out.println("Capacity: " + capacity);
            System.out.println("Refill Time: " + refillTime);
            System.out.println("Refill Amount: " + refillAmount);
            System.out.println("Current Time: " + currentTime);
            Long result = redisTemplate.execute(redisScript, Collections.singletonList(ip),
                    String.valueOf(capacity), String.valueOf(refillTime),
                    String.valueOf(refillAmount), String.valueOf(currentTime));

            System.out.println("Result: " + result);
            return result != null && result == 1;
        } catch (Exception e) {
            e.printStackTrace(); // For quick debugging
            // Log the exception with a logger if available
            // logger.error("Error executing Redis script: ", e);
            return false; // Or handle the error as appropriate for your application
        }
    }

//    public boolean allowRequest(String key, int tokensPerRequest, int capacity, long refillTime, int refillAmount){
//        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(tokenBucketScript, Long.class);
//        List<String> keys = Collections.singletonList(key);
//        Long result = redisTemplate.execute(redisScript, keys,
//                String.valueOf(tokensPerRequest),
//                String.valueOf(capacity),
//                String.valueOf(refillTime),
//                String.valueOf(refillAmount),
//                String.valueOf(System.currentTimeMillis()));
//
//        return result != null && result == 1;
//    }




//    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();
//    private final long capacity;
//    private final long refillTokens;
//    private final long refillPeriod;
//    private final TimeUnit refillUnit;
//
////    public RateLimiterService(long capacity, long refillTokens, long refillPeriod, TimeUnit refillUnit){
////        this.capacity = capacity;
////        this.refillTokens = refillTokens;
////        this.refillPeriod = refillPeriod;
////        this.refillUnit = refillUnit;
////    }
//    public RateLimiterService(){
//        // max 100 tokens, 10 tokens added each minutes
//        this.capacity = 10; //100
//        this.refillTokens = 1; // 10
//        this.refillPeriod = 1;
//        this.refillUnit = TimeUnit.MINUTES;
//    }
//
//    public boolean allowRequest(HttpServletRequest request){
//        String ip = request.getRemoteAddr();
//        Bucket bucket = buckets.computeIfAbsent(ip, k -> new Bucket(capacity, refillTokens, refillPeriod, refillUnit));
//        return bucket.consume();
//    }
//
//    private static class Bucket {
//        private long capacity;
//        private long tokens;
//        private long lastRefillTimestamp;
//        private final long refillTokens;
//        private final long refillPeriodInMilli;
//
//        Bucket(long capacity, long refillTokens, long refillPeriod, TimeUnit refillUnit) {
//            this.capacity = capacity;
//            this.tokens = capacity;
//            this.refillTokens = refillTokens;
//            this.refillPeriodInMilli = refillUnit.toMillis(refillPeriod);
//            this.lastRefillTimestamp = System.currentTimeMillis();
//        }
//
//        synchronized  boolean consume(){
//            refill();
//            if (tokens > 0){
//                tokens--;
//                return true;
//            } else {
//                return false;
//            }
//        }
//
//        private void refill(){
//            long now = System.currentTimeMillis();
//            long elapsedTime = now - lastRefillTimestamp;
//            if(elapsedTime > refillPeriodInMilli){
//                long tokensToAdd = (elapsedTime / refillPeriodInMilli) * refillTokens;
//                tokens = Math.min(capacity, tokens + tokensToAdd);
//                lastRefillTimestamp = now;
//            }
//        }
//    }
}
