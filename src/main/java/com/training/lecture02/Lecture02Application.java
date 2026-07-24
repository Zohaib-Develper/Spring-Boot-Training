package com.training.lecture02;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
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

  public static void main(String[] args) {
    SpringApplication.run(Lecture02Application.class, args);
  }

}