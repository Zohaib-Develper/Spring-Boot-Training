package com.training.lecture02.security;

import com.training.lecture02.users.ApiUser;
import com.training.lecture02.users.ApiUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FormLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ApiUserService apiUserService;
    private final JwtService jwtService;

    public FormLoginSuccessHandler(ApiUserService apiUserService, JwtService jwtService) {
        this.apiUserService = apiUserService;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        ApiUser user = apiUserService.findByUsername(authentication.getName());
        String token = jwtService.generateToken(user);
        response.setContentType("application/json");
        response.getWriter().write("{\"access_token\":\"" + token + "\"}");
    }
}
