package com.example.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Disable CSRF protection
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/**").permitAll() // Permit all requests to /user/**
                        .anyRequest().authenticated() // All other requests should be authenticated
                );
        return http.build();
    }
}
