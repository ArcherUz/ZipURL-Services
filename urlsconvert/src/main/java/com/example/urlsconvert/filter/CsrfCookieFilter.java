package com.example.urlsconvert.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
@Component
public class CsrfCookieFilter extends OncePerRequestFilter {

    private final CsrfTokenRepository csrfTokenRepository = new HttpSessionCsrfTokenRepository();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        CsrfToken existingToken = csrfTokenRepository.loadToken(request);
        if (existingToken == null) {
            CsrfToken csrfToken = csrfTokenRepository.generateToken(request);
            csrfTokenRepository.saveToken(csrfToken, request, response);
            existingToken = csrfToken;
        }

        Cookie csrfCookie = WebUtils.getCookie(request, "XSRF-TOKEN");
        if (csrfCookie == null || !existingToken.getToken().equals(csrfCookie.getValue())) {
            csrfCookie = new Cookie("XSRF-TOKEN", existingToken.getToken());
            csrfCookie.setPath("/");
            csrfCookie.setSecure(request.isSecure());
            csrfCookie.setHttpOnly(false);
            csrfCookie.setMaxAge(-1);
            response.addCookie(csrfCookie);
        }

        filterChain.doFilter(request, response);
    }
}

