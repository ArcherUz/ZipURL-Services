package com.example.urlsconvert.config;

import com.example.urlsconvert.filter.AuthoritiesLoggingAfterFilter;
import com.example.urlsconvert.filter.CsrfCookieFilter;
import com.example.urlsconvert.filter.JWTTokenGeneratorFilter;
import com.example.urlsconvert.filter.JWTTokenValidatorFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                        .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration config = new CorsConfiguration();
                                config.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                                config.setAllowedMethods(Collections.singletonList("*"));
                                config.setAllowCredentials(true);
                                config.setAllowedHeaders(Collections.singletonList("*"));
                                config.setExposedHeaders(Arrays.asList("Authorization", "XSRF-TOKEN"));
                                config.setMaxAge(3600L);
                                return config;
                            }
                        }))

//                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                        .ignoringRequestMatchers("/register", "POST"))
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests((requests) -> requests
                        //.requestMatchers("/api/urls/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/urls/**").permitAll()
                        .requestMatchers("/register").permitAll())
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
//.addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
//.addFilterBefore(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class)
//.addFilterAfter(new JWTTokenGeneratorFilter(), BasicAuthenticationFilter.class)
//.addFilterBefore(new JWTTokenValidatorFilter(), BasicAuthenticationFilter.class)

//                .csrf((csrf) -> csrf.csrfTokenRequestHandler(requestHandler).ignoringRequestMatchers("/register")
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))