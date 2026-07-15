package com.training.lecture02;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnableAdminServer
public class Lecture02Application {

	public static void main(String[] args) {
		SpringApplication.run(Lecture02Application.class, args);
	}

}