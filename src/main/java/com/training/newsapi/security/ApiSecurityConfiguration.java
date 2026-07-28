package com.training.newsapi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableMethodSecurity
public class ApiSecurityConfiguration {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, JwtService jwtService,
      FormLoginSuccessHandler formLoginSuccessHandler,
      Oauth2LoginSuccessHandler oauth2LoginSuccessHandler) {
    http.authorizeHttpRequests(config -> config
            .requestMatchers(
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-resources/**"
            ).permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/news/**", "/api/v1/news", "/api/v1/welcome")
            .permitAll()
            .anyRequest().authenticated())
        .formLogin(config -> config.successHandler(formLoginSuccessHandler))
        .csrf(config ->
            config.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
        .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .oauth2ResourceServer(oauth ->
            oauth.opaqueToken(config -> config.introspector(
                jwtService::introspector
            ))
        )
        .oauth2Login(oauth2 -> oauth2
            .successHandler(oauth2LoginSuccessHandler)
        );
    return http.build();
  }
}
