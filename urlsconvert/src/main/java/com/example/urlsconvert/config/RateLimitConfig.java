package com.example.urlsconvert.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BandwidthBuilder;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Component
public class RateLimitConfig {
    private final Bucket bucket = Bucket.builder()
            .addLimit(Bandwidth.builder()
                    .capacity(10)
                    .refillIntervally(5, Duration.ofSeconds(20))
                    .build())
            .build();
    public Bucket resolveBucket(HttpServletRequest request){
        //limit : 10 requests per minute
//        Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
//        return Bucket.builder().addLimit(limit).build();
//        Bucket bucket = Bucket.builder()
//                //.addLimit(Bandwidth.classic(10,Refill.intervally(10, Duration.ofMinutes(1))))
//                //.addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofSeconds(20))))
//                .addLimit(Bandwidth.builder()
//                        .capacity(10)
//                        .refillIntervally(5, Duration.ofSeconds(20))
//                        .build())
//                .build();
//        return bucket;
        return this.bucket;

    }

}
