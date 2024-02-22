package com.example.urlsconvert.service;

import com.example.urlsconvert.constants.SecurityConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JWTTokenService {
    public String generateToken(String username, Set<GrantedAuthority> authorities) {
        SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));
        String jwt = Jwts.builder()
                .setIssuer("ZipURL")
                .setSubject("JWT TOKEN")
                .claim("username", username)
                .claim("authorities", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 30000000)) // Adjust expiration as needed
                .signWith(key)
                .compact();
        return jwt;
    }
}
