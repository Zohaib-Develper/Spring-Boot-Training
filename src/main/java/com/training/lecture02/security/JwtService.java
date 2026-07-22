package com.training.lecture02.security;

import com.training.lecture02.users.ApiUser;
import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class JwtService {

  private final JwtEncoder jwtEncoder;
  private final JwtDecoder jwtDecoder;

  public String generateToken(ApiUser user) {
    JwsHeader jwsHeader = JwsHeader.with(SignatureAlgorithm.PS256).build();
    JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
        .subject(user.getUsername())
        .claim("role", user.getUserRoles())
        .expiresAt(Instant.now().plusSeconds(5 * 60))
        .build();
    return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, jwtClaimsSet)).getTokenValue();
  }

  public DefaultOAuth2AuthenticatedPrincipal introspector(String token) {
    Jwt jwt = jwtDecoder.decode(token);
    String username = jwt.getSubject();
    String roles = jwt.getClaim("role");
    return new DefaultOAuth2AuthenticatedPrincipal(jwt.getSubject(),
        Map.of("sub", username),
        AuthorityUtils.createAuthorityList(roles.split(",")));
  }
}