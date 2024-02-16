package com.example.urlsconvert.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.csrf((csrf) -> csrf.disable())
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/api/urls/**").authenticated()
                        .requestMatchers("/register").permitAll())
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
//@Bean
//SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
//    http.csrf((csrf) -> csrf.disable())
//            .authorizeHttpRequests((requests) -> requests
//                    .requestMatchers("/**").permitAll())
//            .formLogin(Customizer.withDefaults())
//            .httpBasic(Customizer.withDefaults());
//
//    return http.build();
//}

    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

}