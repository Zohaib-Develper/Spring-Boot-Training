package com.training.lecture02.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.training.lecture02.users.ApiUser;
import com.training.lecture02.users.ApiUserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

class Oauth2LoginSuccessHandlerTest {

  @Test
  void onAuthenticationSuccess_github_shouldUseAuthenticationName() throws IOException, ServletException {
    ApiUserService apiUserService = mock(ApiUserService.class);
    JwtService jwtService = mock(JwtService.class);
    Oauth2LoginSuccessHandler handler = new Oauth2LoginSuccessHandler(apiUserService, jwtService);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    OAuth2AuthenticationToken authentication = mock(OAuth2AuthenticationToken.class);

    StringWriter stringWriter = new StringWriter();
    when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

    when(authentication.getAuthorizedClientRegistrationId()).thenReturn("github");
    when(authentication.getName()).thenReturn("github-user");

    ApiUser user = new ApiUser();
    user.setUsername("github-user");
    when(apiUserService.findOrCreateByEmail("github-user")).thenReturn(user);
    when(jwtService.generateToken(user)).thenReturn("github-token");

    handler.onAuthenticationSuccess(request, response, authentication);

    verify(apiUserService).findOrCreateByEmail("github-user");
    verify(response).setContentType("application/json");
    assertEquals("{\"access_token\":\"github-token\"}", stringWriter.toString().trim());
  }

  @Test
  void onAuthenticationSuccess_googleWithEmail_shouldUseEmail() throws IOException, ServletException {
    ApiUserService apiUserService = mock(ApiUserService.class);
    JwtService jwtService = mock(JwtService.class);
    Oauth2LoginSuccessHandler handler = new Oauth2LoginSuccessHandler(apiUserService, jwtService);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    OAuth2AuthenticationToken authentication = mock(OAuth2AuthenticationToken.class);

    StringWriter stringWriter = new StringWriter();
    when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

    when(authentication.getAuthorizedClientRegistrationId()).thenReturn("google");

    OAuth2User principal = new DefaultOAuth2User(List.of(), Map.of("email", "user@example.com"), "email");
    when(authentication.getPrincipal()).thenReturn(principal);

    ApiUser user = new ApiUser();
    user.setUsername("user@example.com");
    when(apiUserService.findOrCreateByEmail("user@example.com")).thenReturn(user);
    when(jwtService.generateToken(user)).thenReturn("google-token");

    handler.onAuthenticationSuccess(request, response, authentication);

    verify(apiUserService).findOrCreateByEmail("user@example.com");
    verify(response).setContentType("application/json");
    assertEquals("{\"access_token\":\"google-token\"}", stringWriter.toString().trim());
  }

  @Test
  void onAuthenticationSuccess_googleWithoutEmail_shouldUseDefault() throws IOException, ServletException {
    ApiUserService apiUserService = mock(ApiUserService.class);
    JwtService jwtService = mock(JwtService.class);
    Oauth2LoginSuccessHandler handler = new Oauth2LoginSuccessHandler(apiUserService, jwtService);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    OAuth2AuthenticationToken authentication = mock(OAuth2AuthenticationToken.class);

    StringWriter stringWriter = new StringWriter();
    when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

    when(authentication.getAuthorizedClientRegistrationId()).thenReturn("google");

    OAuth2User principal = new DefaultOAuth2User(List.of(), Map.of("sub", "UserFromGoogle"), "sub");
    when(authentication.getPrincipal()).thenReturn(principal);

    ApiUser user = new ApiUser();
    user.setUsername("UserFromGoogle");
    when(apiUserService.findOrCreateByEmail("UserFromGoogle")).thenReturn(user);
    when(jwtService.generateToken(user)).thenReturn("google-token");

    handler.onAuthenticationSuccess(request, response, authentication);

    verify(apiUserService).findOrCreateByEmail("UserFromGoogle");
    verify(response).setContentType("application/json");
    assertEquals("{\"access_token\":\"google-token\"}", stringWriter.toString().trim());
  }
}
