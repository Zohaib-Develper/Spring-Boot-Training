package com.training.newsapi.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.training.newsapi.user.ApiUser;
import com.training.newsapi.user.ApiUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

class FormLoginSuccessHandlerTest {

  @Test
  void onAuthenticationSuccess_shouldWriteJwtToken() throws IOException {
    ApiUserService apiUserService = mock(ApiUserService.class);
    JwtService jwtService = mock(JwtService.class);
    FormLoginSuccessHandler handler = new FormLoginSuccessHandler(apiUserService, jwtService);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    Authentication authentication = mock(Authentication.class);

    StringWriter stringWriter = new StringWriter();
    when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

    ApiUser user = new ApiUser();
    user.setUsername("testuser");
    when(apiUserService.findByUsername("testuser")).thenReturn(user);
    when(jwtService.generateToken(user)).thenReturn("test-token");
    when(authentication.getName()).thenReturn("testuser");

    handler.onAuthenticationSuccess(request, response, authentication);

    verify(response).setContentType("application/json");
    assertEquals("{\"access_token\":\"test-token\"}", stringWriter.toString().trim());
  }
}
