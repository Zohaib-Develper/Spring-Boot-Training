package com.training.lecture02.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ApiSecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(config -> config
                        .requestMatchers(HttpMethod.GET, "/api/v1/news", "/api/v1/news/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/news").hasAnyRole("EDITOR", "ADMIN", "REPORTER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/news/*").hasAnyRole("EDITOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/news/*").hasAnyRole("EDITOR", "ADMIN")
                        .anyRequest().authenticated())
                .formLogin(config -> {
                })
                .csrf(config -> config.disable());

        return http.build();
    }
}
