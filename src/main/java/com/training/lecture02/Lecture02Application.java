package com.training.lecture02;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableAdminServer
@EnableCaching
public class Lecture02Application {

  public static void main(String[] args) {
    SpringApplication.run(Lecture02Application.class, args);
  }

}