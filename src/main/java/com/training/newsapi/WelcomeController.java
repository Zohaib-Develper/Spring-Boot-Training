package com.training.newsapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

  @Value("${initial.message}")
  private String message;

  @GetMapping("/api/v1/welcome")
  public String welcome() {
    return message;
  }
}
