package com.example.urlsconvert.service.impl;

import com.example.urlsconvert.service.UrlService;

import com.example.urlsconvert.dto.UrlResponse;
import com.example.urlsconvert.entity.UrlLongToShort;
import com.example.urlsconvert.exception.UrlException;
import com.example.urlsconvert.repository.UrlLongToShortRepository;
import com.example.urlsconvert.service.UrlService;
import com.example.urlsconvert.utils.UrlValidation;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UrlServiceImpl implements UrlService {
    private final UrlLongToShortRepository urlLongToShortRepository;

    public UrlServiceImpl(UrlLongToShortRepository urlLongToShortRepository){
        this.urlLongToShortRepository = urlLongToShortRepository;
    }

    @Override
    @Transactional
    public UrlResponse encodeShortUrlByMD5(String longUrl) {
        validateUrl(longUrl);
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(longUrl.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for(byte b : digest){
                sb.append(String.format("%02x", b));
            }
            String title = getUrlTitle(longUrl);
            String avatar = getUrlAvatar(longUrl);
            UrlLongToShort url = createOrUpdateUrl(longUrl, sb.toString(), title, avatar);

            UrlResponse urlResponse = UrlResponse.builder().longUrl(longUrl).encodeUrl(sb.toString()).title(title).avatar(avatar).build();
            return urlResponse;

        } catch (NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public UrlResponse encodeShortUrlByBase64(String longUrl) {
        validateUrl(longUrl);
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(longUrl.hashCode());
        String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(buffer.array());

        String title = getUrlTitle(longUrl);
        String avatar = getUrlAvatar(longUrl);
        UrlLongToShort url = createOrUpdateUrl(longUrl, encoded, title, avatar);

        UrlResponse urlResponse = UrlResponse.builder().longUrl(longUrl).encodeUrl(encoded).title(title).avatar(avatar).build();
        return urlResponse;
    }

    @Override
    @Transactional
    public UrlResponse encodeShortUrlByBase62(String longUrl) {
        validateUrl(longUrl);
        int hashCode = Math.abs(longUrl.hashCode());
        final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();
        while (hashCode > 0) {
            sb.insert(0, BASE62.charAt(hashCode % 62));
            hashCode /= 62;
        }

        String title = getUrlTitle(longUrl);
        String avatar = getUrlAvatar(longUrl);
        UrlLongToShort url = createOrUpdateUrl(longUrl, sb.toString(), title, avatar);
        UrlResponse urlResponse = UrlResponse.builder().longUrl(longUrl).encodeUrl(sb.toString()).title(title).avatar(avatar).build();
        return urlResponse;
    }

    @Override
    public String decodeLongUrl(String shortUrl) {
        Optional<UrlLongToShort> urlOptional = urlLongToShortRepository.findByShortUrl(shortUrl).stream().findFirst();
        if(urlOptional.isEmpty()){
            throw new UrlException(HttpStatus.NOT_FOUND, "Short url does not found: " + shortUrl);
        }
        return urlOptional.get().getLongUrl();
    }

    private String getUrlTitle(String url){
        String title = "";
        try{
            title = Jsoup.connect(url).get().select("head > title").text();
        } catch (Exception ex){
            title = "";
        }
        return title;
    }

    private String getUrlAvatar(String url){
        String regex = "^(https?://[^/]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        if(matcher.find()){
            return matcher.group(1) + "/favicon.ico";
        } else {
            return "";
        }
    }

    private void validateUrl(String longUrl){
        if(!UrlValidation.isValidUrl(longUrl)){
            throw new UrlException(HttpStatus.NOT_FOUND, "Url does not valid: " + longUrl);
        }
    }

    private UrlLongToShort createOrUpdateUrl(String longUrl, String shortUrl, String title, String avatar){
        UrlLongToShort url = new UrlLongToShort();
        url.setLongUrl(longUrl);
        url.setShortUrl(shortUrl);
        url.setTitle(title);
        url.setAvatar(avatar);
        urlLongToShortRepository.save(url);

        return url;
    }

}
