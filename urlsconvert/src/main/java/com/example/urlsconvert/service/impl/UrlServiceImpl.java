package com.example.urlsconvert.service.impl;

import com.example.urlsconvert.config.UrlValidation;
import com.example.urlsconvert.dao.UrlRepository;
import com.example.urlsconvert.entity.Url;
import com.example.urlsconvert.rest.UrlNotFoundException;
import com.example.urlsconvert.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Random;
@Service
public class UrlServiceImpl implements UrlService {

    private UrlRepository urlRepository;
    Random random = new Random();

    @Autowired
    public UrlServiceImpl(UrlRepository urlRepository){
        this.urlRepository = urlRepository;
    }

    @Override
    @Transactional
    public Url encodeShortUrlByHash(String longUrl) {
        if (!UrlValidation.isValidUrl(longUrl)){
            throw new UrlNotFoundException("Url does not valid: "+ longUrl);
        }
        Url url = urlRepository.findByLongUrl(longUrl);
        if(url == null){
            Url newUrl = new Url();
            newUrl.setLongUrl(longUrl);
            newUrl.setShortUrl(String.valueOf(newUrl.getLongUrl().hashCode()));
            newUrl.setAccessCount(newUrl.getAccessCount()+1);
            urlRepository.save(newUrl);
            return newUrl;
        } else {
            url.setAccessCount(url.getAccessCount() + 1);
            urlRepository.save(url);
            return url;
        }
    }

    //MD5
    @Override
    public Url encodeShortUrlByMD5(String longUrl) {

        //String prefix = "http://zipurl.com/";

        if (!UrlValidation.isValidUrl(longUrl)){
            throw new UrlNotFoundException("Url does not valid: "+ longUrl);
        }
        Url url = urlRepository.findByLongUrl(longUrl);
        if(url == null){
            Url newUrl = new Url();
            newUrl.setLongUrl(longUrl);

            try{
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(longUrl.getBytes());
                // 128 hash --> 16 byte
                byte[] digest = md.digest();
                StringBuilder sb = new StringBuilder();
                for (byte b : digest){
                    sb.append(String.format("%02x", b));
                }
                newUrl.setShortUrl(sb.toString());
                //newUrl.setShortUrl(String.format("http://zipurl.com/", sb.toString()));

            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }


            newUrl.setAccessCount(newUrl.getAccessCount()+1);
            urlRepository.save(newUrl);
            return newUrl;
        } else {
            url.setAccessCount(url.getAccessCount() + 1);
            urlRepository.save(url);
            return url;
        }

    }

    @Override
    public Url encodeShortUrlByBase64(String longUrl) {
        if (!UrlValidation.isValidUrl(longUrl)){
            throw new UrlNotFoundException("Url does not valid: "+ longUrl);
        }
        Url url = urlRepository.findByLongUrl(longUrl);
        if(url == null){
            Url newUrl = new Url();
            newUrl.setLongUrl(longUrl);
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
            buffer.putInt(newUrl.getLongUrl().hashCode());
            newUrl.setShortUrl(Base64.getEncoder().encodeToString(buffer.array()));
            //newUrl.setShortUrl(String.valueOf(newUrl.getLongUrl().hashCode()));
            newUrl.setAccessCount(newUrl.getAccessCount()+1);
            urlRepository.save(newUrl);
            return newUrl;
        } else {
            url.setAccessCount(url.getAccessCount() + 1);
            urlRepository.save(url);
            return url;
        }
    }

    @Override
    public Url encodeShortUrlByBase62(String longUrl) {
        final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        if (!UrlValidation.isValidUrl(longUrl)){
            throw new UrlNotFoundException("Url does not valid: "+ longUrl);
        }
        Url url = urlRepository.findByLongUrl(longUrl);
        if(url == null){
            Url newUrl = new Url();
            newUrl.setLongUrl(longUrl);
            int hashCode = Math.abs(newUrl.getLongUrl().hashCode());
            StringBuilder sb = new StringBuilder();
            while (hashCode > 0){
                sb.insert(0, BASE62.charAt((int) (hashCode & 62)));
                hashCode /= 62;
            }
            newUrl.setShortUrl(sb.toString());
            //newUrl.setShortUrl(String.valueOf(newUrl.getLongUrl().hashCode()));
            newUrl.setAccessCount(newUrl.getAccessCount()+1);
            urlRepository.save(newUrl);
            return newUrl;
        } else {
            url.setAccessCount(url.getAccessCount() + 1);
            urlRepository.save(url);
            return url;
        }
    }


    @Override
    public List<Url> getAllUrls() {
        return urlRepository.findAll();
    }

    @Override
    public Url decodeLongUrl(String shortUrl) {
//        Url url = urlRepository.findByShortUrl(shortUrl);
//        return url != null ? url.getLongUrl() : null;
        Url url = urlRepository.findByShortUrl(shortUrl);
        if(url == null){
            throw new UrlNotFoundException("Url for "+ shortUrl + " does not exists");
        } else {
            return url;
        }
    }
}
