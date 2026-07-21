package com.training.lecture02.config;

import com.training.lecture02.security.ApiSecurityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import com.training.lecture02.security.JwtService;


@Configuration
public class ApiSecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtService jwtService, ApiSecurityService apiSecurityService) {
        http.authorizeHttpRequests(config -> config
                        .requestMatchers(HttpMethod.GET, "/api/v1/news", "/api/v1/news/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/news").hasAnyAuthority("EDITOR", "REPORTER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/news/*").hasAnyAuthority("EDITOR", "REPORTER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/news/*").hasAnyAuthority("EDITOR")
                        .anyRequest().permitAll())
                .formLogin(config -> {
                    config.successHandler((request, response, auth) -> {
                        String token = apiSecurityService.formLoginSuccessHandler(auth);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"access_token\":" + "\"" + token + "\"}");
                    });
                })
                .csrf(config ->
                        config.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) //with http-only false property allows frontend js to read the cookie value
                                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))//To prevent XOR encoding
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth ->
                        oauth.opaqueToken(config -> config.introspector(
                                jwtService::verify
                        ))
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler((request, response, authentication) -> {
                            String token = apiSecurityService.Oauth2SuccessHandler(authentication);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"access_token\":" + "\"" + token + "\"}");
                        })
                );
        return http.build();
    }
}