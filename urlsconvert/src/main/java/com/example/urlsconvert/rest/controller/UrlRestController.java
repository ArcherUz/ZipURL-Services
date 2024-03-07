package com.example.urlsconvert.rest.controller;

import com.example.urlsconvert.dao.CustomerRepository;
import com.example.urlsconvert.entity.UrlLongToShort;
import com.example.urlsconvert.service.UrlRequestDTO;
import com.example.urlsconvert.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/urls")
public class UrlRestController {
    private UrlService urlService;

    @Autowired
    public UrlRestController(UrlService urlService){
        this.urlService = urlService;
    }

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping
    public ResponseEntity<Set<Map<String, String>>> getAllUrl(){
        return ResponseEntity.ok(urlService.getUrlHistoryByEmail());
    }

    @PostMapping("/md5")
    public ResponseEntity<Map<String, String>> encodeLongUrlByMD5(@RequestBody UrlRequestDTO urlRequestDTO){
        Map<String, String> result = urlService.encodeShortUrlByMD5(urlRequestDTO.getLongUrl());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/base64")
    public ResponseEntity<Map<String, String>> encodeLongUrlByBase64(@RequestBody UrlRequestDTO urlRequestDTO){
        Map<String, String> result = urlService.encodeShortUrlByBase64(urlRequestDTO.getLongUrl());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/base62")
    public ResponseEntity<Map<String, String>> encodeLongUrlByBase62(@RequestBody UrlRequestDTO urlRequestDTO){
        Map<String, String> result = urlService.encodeShortUrlByBase62(urlRequestDTO.getLongUrl());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{shortUrl}") //HttpServletResponse response response.sendRedirect
    public void getLongUrl(@PathVariable String shortUrl, HttpServletResponse response) throws IOException {
        response.sendRedirect(urlService.decodeLongUrl(shortUrl));
    }
}
