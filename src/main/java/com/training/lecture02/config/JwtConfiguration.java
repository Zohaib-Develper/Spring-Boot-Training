package com.training.lecture02.config;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
public class JwtConfiguration {

  @Value("${jwt.private-key}")
  private String privateKeyBase64;

  @Value("${jwt.public-key}")
  private String publicKeyBase64;

  private final SignatureAlgorithm signatureAlgorithm;

  JwtConfiguration() {
    this.signatureAlgorithm = SignatureAlgorithm.PS256;
  }

  @Bean
  JwtEncoder jwtEncoder() throws Exception {
    return NimbusJwtEncoder.withKeyPair(rsaPublicKey(), rsaPrivateKey())
        .algorithm(this.signatureAlgorithm)
        .build();
  }

  @Bean
  JwtDecoder jwtDecoder() throws Exception {
    return NimbusJwtDecoder.withPublicKey(rsaPublicKey())
        .signatureAlgorithm(this.signatureAlgorithm)
        .build();
  }

  private RSAPrivateKey rsaPrivateKey() throws Exception {
    byte[] decoded = Base64.getDecoder().decode(stripPemHeaders(privateKeyBase64));
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    return (RSAPrivateKey) kf.generatePrivate(spec);
  }

  private RSAPublicKey rsaPublicKey() throws Exception {
    byte[] decoded = Base64.getDecoder().decode(stripPemHeaders(publicKeyBase64));
    X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    return (RSAPublicKey) kf.generatePublic(spec);
  }

  private String stripPemHeaders(String pem) {
    return pem
        .replaceAll("-----BEGIN (.*)-----", "")
        .replaceAll("-----END (.*)-----", "")
        .replaceAll("\\s", "");
  }
}