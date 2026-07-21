package com.training.lecture02.security;

import com.training.lecture02.users.ApiUser;
import com.training.lecture02.users.ApiUserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ApiSecurityService {
    private final ApiUserService apiUserService;
    private final JwtService jwtService;

    public String Oauth2SuccessHandler(Authentication authentication) {
        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        String username = "";
        if (registrationId.equals("github")) {
            username = authentication.getName();
        }
        else if (registrationId.equals("google")) {
            OAuth2User principal = (OAuth2User) authentication.getPrincipal();
            username = principal.getAttribute("email");
        }

        ApiUser user = apiUserService.findOrCreateByEmail(username);
        String token = jwtService.generateToken(user);
        return token;
    }

    public String formLoginSuccessHandler(Authentication authentication) {
        ApiUser user = apiUserService.findByUsername(authentication.getName());
        String token = jwtService.generateToken(user);
        return token;
    }
}
