package com.training.lecture02.security;

import com.training.lecture02.users.ApiUser;
import com.training.lecture02.users.ApiUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class Oauth2LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final ApiUserService apiUserService;
  private final JwtService jwtService;

  public Oauth2LoginSuccessHandler(ApiUserService apiUserService, JwtService jwtService) {
    this.apiUserService = apiUserService;
    this.jwtService = jwtService;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {
    String regId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
    String username;
    if ("github".equals(regId)) {
      username = authentication.getName();
    } else {
      OAuth2User principal = (OAuth2User) authentication.getPrincipal();
      username = principal.getAttribute("email");
    }

    ApiUser user = apiUserService.findOrCreateByEmail(username);
    String token = jwtService.generateToken(user);
    response.setContentType("application/json");
    response.getWriter().write("{\"access_token\":\"" + token + "\"}");
  }
}
