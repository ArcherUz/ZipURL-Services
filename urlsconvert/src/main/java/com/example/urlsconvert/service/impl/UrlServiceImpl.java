package com.example.urlsconvert.service.impl;

import com.example.urlsconvert.config.UrlValidation;
import com.example.urlsconvert.dao.UrlRepository;
import com.example.urlsconvert.entity.Url;
import com.example.urlsconvert.rest.UrlNotFoundException;
import com.example.urlsconvert.service.UrlService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UrlServiceImpl implements UrlService {

    private UrlRepository urlRepository;

    @Autowired
    public UrlServiceImpl(UrlRepository urlRepository){
        this.urlRepository = urlRepository;
    }

    public String getUrlTitle(String url){
        String title = "";
        try{
            Document doc = Jsoup.connect(url).get();
            title = doc.select("head > title").text();
        } catch (Exception ex){
            title = ex.toString();
        }
        return title;
    }

    //imgSrc="google.com/favicon.ico"
    public String getUrlAvatarSrc(String url){
        String regex = "^(https?://[^/]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        if(matcher.find()){
            return matcher.group(1) + "/favicon.ico";
        } else {
            return "";
        }
    }



    //MD5
    @Override
    @Transactional
    @Cacheable(value = "urlsByMD5", key= "#longUrl")
    public String encodeShortUrlByMD5(String longUrl) {
        validateUrl(longUrl);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(longUrl.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for(byte b : digest){
                sb.append(String.format("%02x", b));
            }
            createOrUpdateUrl(longUrl, sb.toString());
            String title = getUrlTitle(longUrl);
            String avatar = getUrlAvatarSrc(longUrl);
            return "{" + "shortURL:" + "http://zipurl.com/" + sb.toString() +
                    ", Title:" + title +
                    ", Avatar:" + avatar +
                    "}";

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    @Cacheable(value = "urlsByBase64", key= "#longUrl")
    public String encodeShortUrlByBase64(String longUrl) {
        validateUrl(longUrl);
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(longUrl.hashCode());
        //String encoded = Base64.getEncoder().encodeToString(buffer.array());
        String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(buffer.array());
        createOrUpdateUrl(longUrl, encoded);
        String title = getUrlTitle(longUrl);
        String avatar = getUrlAvatarSrc(longUrl);
        return "{" + "shortURL:" + "http://zipurl.com/" + encoded +
                ", Title:" + title +
                ", Avatar:" + avatar +
                "}";
    }

    @Override
    @Transactional
    @Cacheable(value = "urlsByBase62", key = "#longUrl")
    public String encodeShortUrlByBase62(String longUrl) {
        validateUrl(longUrl);
        int hashCode = Math.abs(longUrl.hashCode());
        final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();
        while (hashCode > 0) {
            sb.insert(0, BASE62.charAt(hashCode % 62));
            hashCode /= 62;
        }
        createOrUpdateUrl(longUrl, sb.toString());

        String title = getUrlTitle(longUrl);
        String avatar = getUrlAvatarSrc(longUrl);
        return "{" + "shortURL:" + "http://zipurl.com/" + sb.toString() +
                ", Title:" + title +
                ", Avatar:" + avatar +
                "}";
    }


    @Override
    @Transactional(readOnly = true)
    public List<Url> getAllUrls() {
        return urlRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "decodedUrls", key = "#shortUrl")
    public String decodeLongUrl(String shortUrl) {
        return urlRepository.findByShortUrl(shortUrl)
                .map(Url::getLongUrl)
                .orElseThrow(() -> new UrlNotFoundException("URL not found for shortURL: " + shortUrl));
    }

    public void validateUrl(String longUrl){
        if (!UrlValidation.isValidUrl(longUrl)){
            throw new UrlNotFoundException("Url does not valid: "+ longUrl);
        }
    }

    //@CachePut(value = "updateUrls", key = "#result.shortUrl")
    private void createOrUpdateUrl(String longUrl, String shortUrl){
        Url url = new Url();
        url.setLongUrl(longUrl);
        url.setShortUrl(shortUrl);
        urlRepository.save(url);
    }
}
