package com.example.urlsconvert.service;

import com.example.urlsconvert.entity.Url;

import java.util.List;

public interface UrlService {
    //Url encodeShortUrlByHash(String longUrl);

    String encodeShortUrlByMD5(String longUrl);

    String encodeShortUrlByBase64(String longUrl);

    String encodeShortUrlByBase62(String longUrl);
    List<Url> getAllUrls();
    String decodeLongUrl(String shortUrl);
}
