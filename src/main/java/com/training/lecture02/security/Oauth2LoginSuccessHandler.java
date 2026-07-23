package com.training.lecture02.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.lecture02.users.ApiUser;
import com.training.lecture02.users.ApiUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
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
      Object principalObj = authentication.getPrincipal();
      if (principalObj instanceof OAuth2User principal) {
        String email = principal.getAttribute("email");
        username = (email != null) ? email : "UserFromGoogle";
      } else {
        username = "UserFromGoogle";
      }
    }
    ApiUser user = apiUserService.findOrCreateByEmail(username);
    String token = jwtService.generateToken(user);
    response.setContentType("application/json");
    new ObjectMapper().writeValue(response.getWriter(), Map.of("access_token", token));
  }
}
