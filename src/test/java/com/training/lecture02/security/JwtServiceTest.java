package com.training.lecture02.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.training.lecture02.users.ApiUser;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

class JwtServiceTest {

  @Test
  void generateToken_shouldReturnJwtToken() {
    JwtEncoder jwtEncoder = mock(JwtEncoder.class);
    JwtDecoder jwtDecoder = mock(JwtDecoder.class);
    JwtService jwtService = new JwtService(jwtEncoder, jwtDecoder);

    ApiUser user = new ApiUser();
    user.setUsername("testuser");
    user.setUserRoles("EDITOR,REPORTER");

    Jwt jwt = new Jwt("token-value", Instant.now(), Instant.now().plusSeconds(300),
        Map.of("alg", "PS256"), Map.of("sub", "testuser", "role", "EDITOR,REPORTER"));
    when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

    String token = jwtService.generateToken(user);

    assertNotNull(token);
    assertEquals("token-value", token);
    verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
  }

  @Test
  void introspector_shouldReturnPrincipal() {
    JwtEncoder jwtEncoder = mock(JwtEncoder.class);
    JwtDecoder jwtDecoder = mock(JwtDecoder.class);
    JwtService jwtService = new JwtService(jwtEncoder, jwtDecoder);

    Jwt jwt = new Jwt("token-value", Instant.now(), Instant.now().plusSeconds(300),
        Map.of("alg", "PS256"), Map.of("sub", "testuser", "role", "EDITOR,REPORTER"));
    when(jwtDecoder.decode("token-value")).thenReturn(jwt);

    DefaultOAuth2AuthenticatedPrincipal principal = jwtService.introspector("token-value");

    assertNotNull(principal);
    assertEquals("testuser", principal.getName());
    assertEquals(List.of(new SimpleGrantedAuthority("EDITOR"), new SimpleGrantedAuthority("REPORTER")),
        principal.getAuthorities().stream().toList());
    verify(jwtDecoder).decode("token-value");
  }
}
