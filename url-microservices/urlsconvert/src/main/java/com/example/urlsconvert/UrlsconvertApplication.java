package com.example.urlsconvert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
//@EnableCaching
@EnableDiscoveryClient
public class UrlsconvertApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrlsconvertApplication.class, args);
    }

}