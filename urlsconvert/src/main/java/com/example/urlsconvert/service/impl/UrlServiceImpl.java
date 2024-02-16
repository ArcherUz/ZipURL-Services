package com.example.urlsconvert.service.impl;

import com.example.urlsconvert.config.UrlValidation;
import com.example.urlsconvert.dao.UrlRepository;
import com.example.urlsconvert.entity.Url;
import com.example.urlsconvert.rest.UrlNotFoundException;
import com.example.urlsconvert.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
@Service
public class UrlServiceImpl implements UrlService {

    private UrlRepository urlRepository;

    @Autowired
    public UrlServiceImpl(UrlRepository urlRepository){
        this.urlRepository = urlRepository;
    }

//    @Override
//    @Transactional
//    @Cacheable(value = "urls", key = "#longUrl")
//    public Url encodeShortUrlByHash(String longUrl) {
//        validateUrl(longUrl);
//        return createOrUpdateUrl(longUrl, String.valueOf(longUrl.hashCode()));
//    }

    //MD5
    @Override
    @Transactional
    @Cacheable(value = "urlsByMD5", key= "#longUrl")
    public Url encodeShortUrlByMD5(String longUrl) {
        validateUrl(longUrl);
        Url url = urlRepository.findByLongUrl(longUrl);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(longUrl.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for(byte b : digest){
                sb.append(String.format("%02x", b));
            }
            return createOrUpdateUrl(longUrl, sb.toString());
            // return string short url

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    @Cacheable(value = "urlsByBase64", key= "#longUrl")
    public Url encodeShortUrlByBase64(String longUrl) {
        validateUrl(longUrl);
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(longUrl.hashCode());
        //String encoded = Base64.getEncoder().encodeToString(buffer.array());
        String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(buffer.array());
        return createOrUpdateUrl(longUrl, encoded);
    }

    @Override
    @Transactional
    @Cacheable(value = "urlsByBase62", key = "#longUrl")
    public Url encodeShortUrlByBase62(String longUrl) {
        validateUrl(longUrl);
        int hashCode = Math.abs(longUrl.hashCode());
        final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();
        while (hashCode > 0) {
            sb.insert(0, BASE62.charAt(hashCode % 62));
            hashCode /= 62;
        }
        return createOrUpdateUrl(longUrl, sb.toString());
    }


    @Override
    @Transactional(readOnly = true)
    public List<Url> getAllUrls() {
        return urlRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "decodedUrls", key = "#shortUrl")
    public Url decodeLongUrl(String shortUrl) {
        return urlRepository.findByShortUrl(shortUrl)
                .orElseThrow(() -> new UrlNotFoundException("URL not found for shortURL: " + shortUrl));
    }

    public void validateUrl(String longUrl){
        if (!UrlValidation.isValidUrl(longUrl)){
            throw new UrlNotFoundException("Url does not valid: "+ longUrl);
        }
    }

    //@CachePut(value = "updateUrls", key = "#result.shortUrl")
    private Url createOrUpdateUrl(String longUrl, String shortUrl){
        Url url = urlRepository.findByLongUrl(longUrl);
        if(url == null){
            url = new Url();
            url.setLongUrl(longUrl);
            url.setShortUrl(shortUrl);
        }
        url.setAccessCount(url.getAccessCount() + 1);
        urlRepository.save(url);
        return url;
    }
}
