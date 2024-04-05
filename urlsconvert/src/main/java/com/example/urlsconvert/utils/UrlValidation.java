package com.example.urlsconvert.utils;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlValidation {
    public static boolean isValidUrl(String url){
        try{
            new URL(url);
            return true;
        } catch (MalformedURLException e){
            return false;
        }
    }
}
