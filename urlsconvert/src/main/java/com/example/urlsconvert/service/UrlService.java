package com.example.urlsconvert.service;

import com.example.urlsconvert.entity.Url;

import java.util.List;

public interface UrlService {
    //Url encodeShortUrlByHash(String longUrl);

    Url encodeShortUrlByMD5(String longUrl);

    Url encodeShortUrlByBase64(String longUrl);

    Url encodeShortUrlByBase62(String longUrl);
    List<Url> getAllUrls();
    Url decodeLongUrl(String shortUrl);
}
