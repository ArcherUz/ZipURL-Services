package com.example.service;

import com.example.dto.UrlRequestDto;
import com.example.dto.UrlResponseDto;
import com.example.event.EncodeUrlEvent;
import com.example.repository.CustomerRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class CustomerEncodeUrlService {
    private final CustomerRepository customerRepository;
    private final WebClient.Builder webClientBuilder;
    private final JwtDecoder jwtDecoder;
    private final KafkaTemplate<String, EncodeUrlEvent> kafkaTemplate;


    public CustomerEncodeUrlService (CustomerRepository customerRepository, WebClient.Builder webClientBuilder, JwtDecoder jwtDecoder, KafkaTemplate kafkaTemplate){
        this.customerRepository = customerRepository;
        this.webClientBuilder = webClientBuilder;
        this.jwtDecoder = jwtDecoder;
        this.kafkaTemplate = kafkaTemplate;
    }

    public UrlResponseDto fetchEncodeUrlByBase62(UrlRequestDto urlRequestDTO, String authorizationHeader) {
        String email = extractEmailFromJWT(authorizationHeader);
        WebClient webClient = webClientBuilder.build();
        UrlResponseDto urlResponseDto = webClient.post()
                .uri("http://urlsconvert/api/urls/base62")
                .header("Authorization", authorizationHeader)
                .bodyValue(urlRequestDTO)
                .retrieve()
                .bodyToMono(UrlResponseDto.class)
                .doOnNext(response -> updateCustomerUrlHistory(email, urlRequestDTO.getLongUrl()))
                .block();

        kafkaTemplate.send("notificationTopic", new EncodeUrlEvent(urlResponseDto.getEncodeUrl(), email));
        return urlResponseDto;
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
