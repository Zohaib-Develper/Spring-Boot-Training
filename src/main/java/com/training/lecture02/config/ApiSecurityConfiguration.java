package com.training.lecture02.config;

import com.training.lecture02.users.ApiUser;
import com.training.lecture02.users.ApiUserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import com.training.lecture02.security.JwtService;
import com.training.lecture02.security.JwtAuthenticationConverter;


@Configuration
public class ApiSecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, ApiUserService userService,
                                           JwtService jwtService, JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
        http.authorizeHttpRequests(config -> config
                        .requestMatchers(HttpMethod.GET, "/api/v1/news", "/api/v1/news/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/news").hasAnyRole("EDITOR", "REPORTER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/news/*").hasAnyRole("EDITOR", "REPORTER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/news/*").hasAnyRole("EDITOR")
                        .anyRequest().permitAll())
                .formLogin(config -> {
                    config.successHandler((request, response, auth) -> {
                        ApiUser user = userService.findByUsername(auth.getName());
                        String token = jwtService.generateToken(user);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"access_token\":" + "\"" + token + "\"}");
                    });
                })
                .csrf(config ->
                        config.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) //with http-only false property allows frontend js to read the cookie value
                                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))//To prevent XOR encoding
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth ->
                        oauth.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler((request, response, authentication) -> {
                            OAuth2User principal = (OAuth2User) authentication.getPrincipal();
                            String email = principal.getAttribute("email");

                            if (email == null) {
                                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email not provided by provider");
                                return;
                            }

                            ApiUser user = userService.findOrCreateByEmail(email);
                            String token = jwtService.generateToken(user);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"access_token\":" + "\"" + token + "\"}");
                        })
                );
        return http.build();
    }
}