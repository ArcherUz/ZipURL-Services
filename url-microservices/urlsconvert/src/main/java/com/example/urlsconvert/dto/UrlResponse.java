package com.example.urlsconvert.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UrlResponse{
    private String longUrl;
    private String encodeUrl;
    private String title;
    private String avatar;

    //private static final String BASE_URL = "http://localhost:8080/api/urls/";

    public void setEncodeUrl(String encodeUrl) {
        this.encodeUrl = getBaseUrl() + encodeUrl;
    }

    private String getBaseUrl() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (sra != null) {
            return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/api/urls/";
        }
        // Default URL or throw an exception if the context is not available
        return "http://localhost:8080/api/urls/";
    }

    public static class UrlResponseBuilder {
        private String encodeUrl;

        public UrlResponseBuilder encodeUrl(String encodeUrl) {
            this.encodeUrl = build().getBaseUrl() + encodeUrl; // Assume base URL will be prepended in the setter
            return this;
        }
    }
}
