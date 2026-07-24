package com.training.lecture02;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
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
public class Lecture02Application {

  static {
    init();
  }

  public static void init() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    Locale.setDefault(Locale.US);
  }

  public static void main(String[] args) {
    SpringApplication.run(Lecture02Application.class, args);
  }

}