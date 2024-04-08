package com.example.service;

import com.example.dto.UrlRequestDto;
import com.example.dto.UrlResponseDto;
import com.example.entity.Customer;
import com.example.repository.CustomerRepository;
import com.netflix.discovery.EurekaClient;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Service
public class CustomerEncodeUrlService {
    private final CustomerRepository customerRepository;
    private final WebClient.Builder webClientBuilder;
    private final JwtDecoder jwtDecoder;


    public CustomerEncodeUrlService (CustomerRepository customerRepository, WebClient.Builder webClientBuilder, JwtDecoder jwtDecoder){
        this.customerRepository = customerRepository;
        this.webClientBuilder = webClientBuilder;
        this.jwtDecoder = jwtDecoder;
    }

    public Mono<UrlResponseDto> fetchEncodeUrlByMD5(UrlRequestDto urlRequestDTO, String authorizationHeader) {
        String email = extractEmailFromJWT(authorizationHeader);
        WebClient webClient = webClientBuilder.build();
        return webClient.post()
                .uri("http://urlsconvert/api/urls/md5")
                .header("Authorization", authorizationHeader)
                .bodyValue(urlRequestDTO)
                .retrieve()
                .bodyToMono(UrlResponseDto.class)
                .doOnNext(response -> updateCustomerUrlHistory(email, urlRequestDTO.getLongUrl()));
    }

    private String  extractEmailFromJWT(String jwtToken){
        String token = jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getClaimAsString("email");
    }

    public void updateCustomerUrlHistory(String email, String longUrl){
        Mono.justOrEmpty(email)
                .flatMap(e -> Mono.justOrEmpty(customerRepository.findByEmail(e)))
                .doOnNext(customer -> {
                    customer.addLongUrl(longUrl);
                    customerRepository.save(customer);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }
}
