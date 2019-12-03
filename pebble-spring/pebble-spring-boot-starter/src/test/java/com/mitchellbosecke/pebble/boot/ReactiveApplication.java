package com.mitchellbosecke.pebble.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.springframework.boot.WebApplicationType.REACTIVE;

@SpringBootApplication
public class ReactiveApplication {

  public static void main(String[] args) {
    SpringApplication application = new SpringApplication(Application.class);
    application.setWebApplicationType(REACTIVE);
    application.run(args);
  }
}