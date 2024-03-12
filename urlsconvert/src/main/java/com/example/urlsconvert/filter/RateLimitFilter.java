package com.example.urlsconvert.filter;

import com.example.urlsconvert.config.RateLimitConfig;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private final RateLimitConfig rateLimitConfig;

    @Autowired
    public RateLimitFilter(RateLimitConfig rateLimitConfig){
        this.rateLimitConfig = rateLimitConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Bucket bucket = rateLimitConfig.resolveBucket(request);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if(probe.isConsumed()){
            filterChain.doFilter(request, response);
        } else {
            response.setContentType("application/json");
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            response.getWriter().append(String.format("Rate limit exceeded. Try again in %d second." , waitForRefill));
        }
    }
}
