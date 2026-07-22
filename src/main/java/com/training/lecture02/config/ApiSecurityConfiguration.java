package com.training.lecture02.config;

import com.training.lecture02.security.FormLoginSuccessHandler;
import com.training.lecture02.security.JwtService;
import com.training.lecture02.security.Oauth2LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;


@Configuration
public class ApiSecurityConfiguration {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, JwtService jwtService,
      FormLoginSuccessHandler formLoginSuccessHandler,
      Oauth2LoginSuccessHandler oauth2LoginSuccessHandler) {
    http.authorizeHttpRequests(config -> config
            .requestMatchers(HttpMethod.GET, "/api/v1/news", "/api/v1/news/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/news").hasAnyAuthority("EDITOR", "REPORTER")
            .requestMatchers(HttpMethod.PUT, "/api/v1/news/*").hasAnyAuthority("EDITOR", "REPORTER")
            .requestMatchers(HttpMethod.DELETE, "/api/v1/news/*").hasAnyAuthority("EDITOR")
            .anyRequest().permitAll())
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