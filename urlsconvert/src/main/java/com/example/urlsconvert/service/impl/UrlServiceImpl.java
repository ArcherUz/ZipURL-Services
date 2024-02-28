package com.example.urlsconvert.service.impl;

import com.example.urlsconvert.config.UrlValidation;
import com.example.urlsconvert.dao.CustomerRepository;
import com.example.urlsconvert.dao.UrlRepository;
import com.example.urlsconvert.entity.Customer;
import com.example.urlsconvert.entity.Url;
import com.example.urlsconvert.rest.UrlNotFoundException;
import com.example.urlsconvert.service.UrlService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UrlServiceImpl implements UrlService {

    private UrlRepository urlRepository;

    @Autowired
    public UrlServiceImpl(UrlRepository urlRepository){
        this.urlRepository = urlRepository;
    }

    @Autowired
    private CustomerRepository customerRepository;

    private Customer getCustomer(){
        Customer customer = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<Customer> customerOptional = customerRepository.findByEmail(email).stream().findFirst();
        if(!customerOptional.isPresent()){
            return customer;
        }
        customer = customerOptional.get();
        return customer;
    }

    private String getUrlTitle(String url){
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
    private String getUrlAvatarSrc(String url){
        String regex = "^(https?://[^/]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        if(matcher.find()){
            return matcher.group(1) + "/favicon.ico";
        } else {
            return "";
        }
    }

    private Map<String, String> buildResponse(String longUrl,String encodeUrl, String title, String avatar){
        Map<String, String> response = new HashMap<>();
        response.put("longURL", longUrl);
        response.put("shortURL", "http://zipurl.com/" +encodeUrl);
        response.put("Title", title);
        response.put("Avatar", avatar);
        return response;
    }



    //MD5
    @Override
    @Transactional
    @Cacheable(value = "urlsByMD5", key= "#longUrl")
    public Map<String, String> encodeShortUrlByMD5(String longUrl) {
        validateUrl(longUrl);
        Customer customer = getCustomer();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(longUrl.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for(byte b : digest){
                sb.append(String.format("%02x", b));
            }

            String title = getUrlTitle(longUrl);
            String avatar = getUrlAvatarSrc(longUrl);
            Url url = createOrUpdateUrl(longUrl, sb.toString(), title, avatar);

            if (customer != null){
                customer.addUrls(url);
                customerRepository.save(customer);
            }
            return buildResponse(longUrl, sb.toString(), title, avatar);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    @Cacheable(value = "urlsByBase64", key= "#longUrl")
    public Map<String, String> encodeShortUrlByBase64(String longUrl) {
        validateUrl(longUrl);
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(longUrl.hashCode());
        String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(buffer.array());

        String title = getUrlTitle(longUrl);
        String avatar = getUrlAvatarSrc(longUrl);
        Url url = createOrUpdateUrl(longUrl, encoded, title, avatar);
        Customer customer = getCustomer();
        if(customer!=null){
            customer.addUrls(url);
            customerRepository.save(customer);
        }
        return buildResponse(longUrl, encoded, title, avatar);
    }

    @Override
    @Transactional
    @Cacheable(value = "urlsByBase62", key = "#longUrl")
    public Map<String, String> encodeShortUrlByBase62(String longUrl) {
        validateUrl(longUrl);
        int hashCode = Math.abs(longUrl.hashCode());
        final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();
        while (hashCode > 0) {
            sb.insert(0, BASE62.charAt(hashCode % 62));
            hashCode /= 62;
        }

        String title = getUrlTitle(longUrl);
        String avatar = getUrlAvatarSrc(longUrl);
        Url url = createOrUpdateUrl(longUrl, sb.toString(), title, avatar);
        Customer customer = getCustomer();
        if(customer!=null){
            customer.addUrls(url);
            customerRepository.save(customer);
        }
        return buildResponse(longUrl, sb.toString(), title, avatar);
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
    private Url createOrUpdateUrl(String longUrl, String shortUrl, String title, String avatar){
        Url url = new Url();
        url.setLongUrl(longUrl);
        url.setShortUrl(shortUrl);
        url.setTitle(title);
        url.setAvatar(avatar);
        urlRepository.save(url);
        return url;
    }
}
