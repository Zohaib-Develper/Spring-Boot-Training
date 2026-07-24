package com.training.lecture02;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class WelcomeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void welcome_shouldReturnWelcomeMessage() throws Exception {
    mockMvc.perform(get("/api/v1/welcome"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello from application.yaml"));
  }
}
