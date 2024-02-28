package com.example.urlsconvert.config;

import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlValidation {
    public static boolean isValidUrl(String urlString){
        try{
            new URL(urlString);
            return true;
        } catch (MalformedURLException e){
            return false;
        }
    }
}
