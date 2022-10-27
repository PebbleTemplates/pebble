/*
 * Copyright (c) 2013 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.spring.config;

import io.pebbletemplates.PebbleEngine;
import io.pebbletemplates.loader.ClasspathLoader;
import io.pebbletemplates.loader.Loader;
import io.pebbletemplates.spring.bean.SomeBean;
import io.pebbletemplates.spring.extension.SpringExtension;
import io.pebbletemplates.spring.servlet.PebbleViewResolver;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.ViewResolver;

/**
 * Spring configuration for unit test
 *
 * @author Eric Bussieres
 */
@Configuration(proxyBeanMethods = false)
public class MVCConfig {

  @Bean
  public SomeBean foo() {
    return new SomeBean();
  }

  @Bean
  public MessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename("io.pebbletemplates.spring.messages");

    return messageSource;
  }

  @Bean
  public PebbleEngine pebbleEngine(SpringExtension springExtension,
                                   Loader<?> templateLoader) {
    return new PebbleEngine.Builder()
        .loader(templateLoader)
        .strictVariables(false)
        .extension(springExtension)
        .build();
  }

  @Bean
  public SpringExtension springExtension(MessageSource messageSource) {
    return new SpringExtension(messageSource);
  }

  @Bean
  public Loader<?> templateLoader() {
    return new ClasspathLoader();
  }

  @Bean
  public ViewResolver viewResolver(PebbleEngine pebbleEngine) {
    PebbleViewResolver viewResolver = new PebbleViewResolver(pebbleEngine);
    viewResolver.setPrefix("io/pebbletemplates/spring/template/");
    viewResolver.setSuffix(".html");
    viewResolver.setContentType("text/html");
    return viewResolver;
  }
}
