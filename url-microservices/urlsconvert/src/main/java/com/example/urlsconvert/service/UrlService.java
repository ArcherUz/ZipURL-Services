package com.example.urlsconvert.service;

import com.example.urlsconvert.dto.UrlResponse;

import java.util.List;

public interface UrlService {
    UrlResponse encodeShortUrlByMD5(String longUrl);
    UrlResponse encodeShortUrlByBase64(String longUrl);
    UrlResponse encodeShortUrlByBase62(String longUrl);
    String decodeLongUrl(String shortUrl);
    List<UrlResponse> findUrlByListId(List<String> ids);
}
