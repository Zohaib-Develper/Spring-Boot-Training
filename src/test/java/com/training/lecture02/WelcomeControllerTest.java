package com.training.lecture02;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
    properties = {
        "spring.security.oauth2.client.registration.google.client-id=dummy",
        "spring.security.oauth2.client.registration.google.client-secret=dummy",
        "spring.security.oauth2.client.registration.github.client-id=dummy",
        "spring.security.oauth2.client.registration.github.client-secret=dummy"
    }
)
@AutoConfigureMockMvc
public class WelcomeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @DynamicPropertySource
  static void jwtProperties(DynamicPropertyRegistry registry) throws IOException {
    String privateKey = Files.readString(Path.of("src/test/resources/private_key.pem"));
    String publicKey = Files.readString(Path.of("src/test/resources/public_key.pem"));
    registry.add("jwt.private-key", () -> privateKey);
    registry.add("jwt.public-key", () -> publicKey);
  }

  @Test
  void welcome_shouldReturnWelcomeMessage() throws Exception {
    mockMvc.perform(get("/api/v1/welcome"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello from application.yaml"));
  }
}
