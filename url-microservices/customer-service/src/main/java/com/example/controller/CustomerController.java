package com.example.controller;

import com.example.dto.CustomerRequestDto;
import com.example.dto.UrlRequestDto;
import com.example.dto.UrlResponseDto;
import com.example.service.CustomerEncodeUrlService;
import com.example.service.CustomerService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/user")
public class CustomerController {
    private final CustomerService customerService;
    private final CustomerEncodeUrlService customerEncodeUrlService;
    public CustomerController (CustomerService customerService, CustomerEncodeUrlService customerEncodeUrlService){
        this.customerService = customerService;
        this.customerEncodeUrlService = customerEncodeUrlService;
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody CustomerRequestDto customerRequestDto){
        return customerService.registerCustomer(customerRequestDto.getEmail(), customerRequestDto.getPassword());
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody CustomerRequestDto customerRequestDto){
        return customerService.login(customerRequestDto.getEmail(), customerRequestDto.getPassword());
    }

    @PostMapping("/encode/md5")
    @CircuitBreaker(name = "urlsconvert", fallbackMethod = "fallbackMethod")
    @TimeLimiter(name = "urlsconvert")
    @Retry(name = "urlsconvert")
    public CompletableFuture<UrlResponseDto> encodeUrlByBase62(@RequestBody UrlRequestDto urlRequestDTO, @RequestHeader("Authorization") String authorizationHeader) {
        return CompletableFuture.supplyAsync(() -> customerEncodeUrlService.fetchEncodeUrlByBase62(urlRequestDTO, authorizationHeader)) ;
    }

    public CompletableFuture<String> fallbackMethod(UrlRequestDto urlRequestDto, RuntimeException runtimeException){
        return CompletableFuture.supplyAsync(() -> "Oops! Something went wrong, please try again later");
    }
}
