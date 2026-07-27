package com.training.newsapi;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import java.util.Locale;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableAdminServer
@EnableCaching
@EnableAsync
@EnableScheduling
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class NewsApiApplication {

  static {
    init();
  }

  public static void init() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    Locale.setDefault(Locale.US);
  }

  public static void main(String[] args) {
    SpringApplication.run(NewsApiApplication.class, args);
  }

}