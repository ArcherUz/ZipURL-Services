package com.example.urlsconvert.filter;

import com.example.urlsconvert.rest.InvalidJwtTokenException;
import com.example.urlsconvert.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtRequestFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public JwtRequestFilter(UserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try{
                username = jwtUtil.getUsernameFromToken(jwt);
            } catch (Exception ex){
                throw new InvalidJwtTokenException("JWT token is invalid or expired.");
            }

        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try{
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if(!jwtUtil.validateToken(jwt, userDetails.getUsername())){
                    throw new InvalidJwtTokenException("JWT token is invalid or expired.");
                }

                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception ex){
                throw new InvalidJwtTokenException("JWT token is invalid or expired.");
            }

        }
        filterChain.doFilter(request, response);
    }

}
