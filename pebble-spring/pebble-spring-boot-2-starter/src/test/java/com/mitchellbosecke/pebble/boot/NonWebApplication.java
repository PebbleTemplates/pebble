package com.mitchellbosecke.pebble.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;

@SpringBootApplication
public class NonWebApplication {

  public static void main(String[] args) {
    SpringApplication sa = new SpringApplicationBuilder(NonWebApplication.class)
        .web(WebApplicationType.NONE)
        .build();
    sa.run(args);
  }

  @Bean
  public MessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename("messages");
    messageSource.setFallbackToSystemLocale(false);
    return messageSource;
  }

}
