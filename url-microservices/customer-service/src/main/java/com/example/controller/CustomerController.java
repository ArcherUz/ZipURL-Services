package com.example.controller;

import com.example.dto.CustomerRequestDto;
import com.example.dto.UrlRequestDto;
import com.example.dto.UrlResponseDto;
import com.example.service.CustomerEncodeUrlService;
import com.example.service.CustomerService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

//    @PostMapping("/encode/md5")
//    public Mono<ResponseEntity<UrlResponseDto>> userEncodeMd5(@RequestBody String longUrl){
//        return customerEncodeUrlService.fetchEncodeUrlByMD5(longUrl);
//    }

    @PostMapping("/encode/md5")
    public Mono<UrlResponseDto> encodeUrlByMD5(@RequestBody UrlRequestDto urlRequestDTO, @RequestHeader("Authorization") String authorizationHeader) {
//        WebClient webClient = webClientBuilder.build();
//
//        return webClient.post()
//                .uri("http://urlsconvert/api/urls/md5")
//                .header("Authorization", authorizationHeader)
//                .bodyValue(urlRequestDTO)
//                .retrieve()
//                .bodyToMono(UrlResponseDto.class);
        return customerEncodeUrlService.fetchEncodeUrlByMD5(urlRequestDTO, authorizationHeader);
    }
}
