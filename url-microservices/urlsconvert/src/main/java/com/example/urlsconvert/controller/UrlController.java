package com.example.urlsconvert.controller;

import com.example.urlsconvert.dto.UrlRequestDto;
import com.example.urlsconvert.dto.UrlResponse;
import com.example.urlsconvert.entity.UrlLongToShort;
import com.example.urlsconvert.repository.UrlLongToShortRepository;
import com.example.urlsconvert.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/urls")
public class UrlController {

    private final UrlLongToShortRepository urlRepository;
    private final UrlService urlService;
    @GetMapping()
    public List<UrlLongToShort> getAllURL(){
        return urlRepository.findAll();
    }

    @PostMapping("/md5")
    public ResponseEntity<UrlResponse> encodeLongUrlByMD5(@RequestBody UrlRequestDto urlRequestDTO){
        UrlResponse result = urlService.encodeShortUrlByMD5(urlRequestDTO.getLongUrl());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/base64")
    public ResponseEntity<UrlResponse> encodeLongUrlByBase64(@RequestBody UrlRequestDto urlRequestDTO){
        UrlResponse result = urlService.encodeShortUrlByBase64(urlRequestDTO.getLongUrl());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/base62")
    public ResponseEntity<UrlResponse> encodeLongUrlByBase62(@RequestBody UrlRequestDto urlRequestDTO){
        UrlResponse result = urlService.encodeShortUrlByBase62(urlRequestDTO.getLongUrl());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{shortUrl}") //HttpServletResponse response response.sendRedirect
    public void getLongUrl(@PathVariable String shortUrl, HttpServletResponse response) throws IOException {
        response.sendRedirect(urlService.decodeLongUrl(shortUrl));
    }

    @PostMapping("/ids")
    public List<UrlResponse> getUrlByListId(@RequestBody List<String> ids){
        return urlService.findUrlByListId(ids);
    }

}