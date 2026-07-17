package com.training.lecture02.config;

import com.training.lecture02.users.ApiUser;
import com.training.lecture02.users.ApiUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import java.util.Arrays;
import java.util.Map;

@Configuration
public class ApiSecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, ApiUserService userService) throws Exception {
        http.authorizeHttpRequests(config -> config
                        .requestMatchers(HttpMethod.GET, "/api/v1/news", "/api/v1/news/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/news").hasAnyRole("EDITOR", "REPORTER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/news/*").hasAnyRole("EDITOR", "REPORTER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/news/*").hasAnyRole("EDITOR")
                        .anyRequest().authenticated())
                .formLogin(config -> {
                    config.successHandler((request, response, auth) -> {
                        String token = userService.generateToken(auth.getName());
                        response.setContentType("application/json");
                        response.getWriter().write("{\"access_token\":" + "\"" + token + "\"}");
                    });
                })
                .csrf(config ->
                        config.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) //with http-only false property allows frontend js to read the cookie value
                                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))//To prevent XOR encoding
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(config -> config.opaqueToken(config2 -> config2.introspector(
                        token -> {
                            ApiUser user = userService.findByToken(token);
                            var authorities = AuthorityUtils.createAuthorityList(
                                    Arrays.stream(user.getUserRoles().split(","))
                                            .map(role -> "ROLE_" + role.trim())
                                            .toArray(String[]::new)
                            );
                            return new DefaultOAuth2AuthenticatedPrincipal(
                                    user.getUsername(), Map.of("sub", user.getUsername()), authorities
                            );
                        }
                )));


        return http.build();
    }
}
