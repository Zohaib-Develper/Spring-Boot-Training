package com.training.lecture02;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConfigurationProperties(prefix = "initial")
public class WelcomeController {

  private String message;

  public void setMessage(String message) {
    this.message = message;
  }

  @GetMapping("/api/v1/welcome")
  public String welcome() {
    return message;
  }
}
