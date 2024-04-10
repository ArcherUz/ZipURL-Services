package com.example.urlsconvert.service;

import com.example.urlsconvert.dto.UrlResponse;

public interface UrlService {
    UrlResponse encodeShortUrlByMD5(String longUrl);
    UrlResponse encodeShortUrlByBase64(String longUrl);
    UrlResponse encodeShortUrlByBase62(String longUrl);
    String decodeLongUrl(String shortUrl);
}
