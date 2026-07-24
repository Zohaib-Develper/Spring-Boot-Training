package com.training.lecture02.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class FormLoginSuccessHandlerTest {

  @Test
  void onAuthenticationSuccess_shouldWriteJwtToken() throws IOException, ServletException {
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
