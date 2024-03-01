package com.example.urlsconvert.service;

import com.example.urlsconvert.entity.Url;

import java.util.List;
import java.util.Map;

public interface UrlService {
    //Url encodeShortUrlByHash(String longUrl);

    Map<String, String> encodeShortUrlByMD5(String longUrl);

    Map<String, String> encodeShortUrlByBase64(String longUrl);

    Map<String, String> encodeShortUrlByBase62(String longUrl);
    List<Url> getAllUrls();
    String decodeLongUrl(String shortUrl);
}
