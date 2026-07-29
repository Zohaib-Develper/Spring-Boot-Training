package com.training.newsapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

  @GetMapping("/api/v1/welcome")
  public String welcome() {
    return "Hello from WelcomeController";
  }
}
