package com.training.lecture02;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class WelcomeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void welcomeApi_ShouldReturnMessageFromApplicationYaml() throws Exception {
    mockMvc.perform(
            get("/api/v1/welcome").with(SecurityMockMvcRequestPostProcessors.user("editor")))
        .andExpect(MockMvcResultMatchers.content().string("Hello from controller"));
  }
}
