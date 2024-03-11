package com.example.urlsconvert.service;

import com.example.urlsconvert.entity.UrlLongToShort;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UrlService {
    //Url encodeShortUrlByHash(String longUrl);
    Map<String, String> encodeShortUrlByMD5(String longUrl);
    Map<String, String> encodeShortUrlByBase64(String longUrl);
    Map<String, String> encodeShortUrlByBase62(String longUrl);
    //List<UrlLongToShort> getAllUrls();
    Set<Map<String, String>> getUrlHistoryByEmail();
    String decodeLongUrl(String shortUrl);
}
