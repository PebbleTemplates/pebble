package com.mitchellbosecke.pebble.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class NonWebApplication {

  public static void main(String[] args) {
    SpringApplication sa = new SpringApplicationBuilder(NonWebApplication.class)
        .web(WebApplicationType.NONE)
        .build();
    sa.run(args);
  }
}
