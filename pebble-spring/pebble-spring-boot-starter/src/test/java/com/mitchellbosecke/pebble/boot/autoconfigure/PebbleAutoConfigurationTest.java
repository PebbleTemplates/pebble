package com.mitchellbosecke.pebble.boot.autoconfigure;

import static java.util.Locale.CHINESE;
import static org.assertj.core.api.Assertions.assertThat;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.spring.extension.SpringExtension;
import com.mitchellbosecke.pebble.spring.reactive.PebbleReactiveViewResolver;
import com.mitchellbosecke.pebble.spring.servlet.PebbleViewResolver;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebApplicationContext;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

class PebbleAutoConfigurationTest {

  private static final Locale DEFAULT_LOCALE = CHINESE;
  private AnnotationConfigServletWebApplicationContext webContext;

  private AnnotationConfigReactiveWebApplicationContext reactiveWebContext;

  @Test
  void registerBeansForServletApp() {
    this.loadWithServlet(null);
    assertThat(this.webContext.getBeansOfType(Loader.class)).hasSize(1);
    assertThat(this.webContext.getBeansOfType(SpringExtension.class)).hasSize(1);
    assertThat(this.webContext.getBeansOfType(PebbleEngine.class)).hasSize(1);
    assertThat(this.webContext.getBeansOfType(PebbleViewResolver.class)).hasSize(1);
  }

  @Test
  void registerCompilerForServletApp() {
    this.loadWithServlet(CustomPebbleEngineCompilerConfiguration.class);
    assertThat(this.webContext.getBeansOfType(Loader.class)).hasSize(1);
    assertThat(this.webContext.getBeansOfType(SpringExtension.class)).hasSize(1);
    assertThat(this.webContext.getBeansOfType(PebbleEngine.class)).hasSize(1);
    assertThat(this.webContext.getBean(PebbleEngine.class).getDefaultLocale()).isEqualTo(DEFAULT_LOCALE);
    assertThat(this.webContext.getBeansOfType(PebbleViewResolver.class)).hasSize(1);
    assertThat(this.webContext.getBeansOfType(PebbleReactiveViewResolver.class)).isEmpty();
  }

  @Test
  void registerBeansForReactiveApp() {
    this.loadWithReactive(null);
    assertThat(this.reactiveWebContext.getBeansOfType(Loader.class)).hasSize(1);
    assertThat(this.reactiveWebContext.getBeansOfType(SpringExtension.class)).hasSize(1);
    assertThat(this.reactiveWebContext.getBeansOfType(PebbleEngine.class)).hasSize(1);
    assertThat(this.reactiveWebContext.getBeansOfType(PebbleViewResolver.class)).isEmpty();
    assertThat(this.reactiveWebContext.getBeansOfType(PebbleReactiveViewResolver.class)).hasSize(1);
    assertThat(this.reactiveWebContext.getBeansOfType(PebbleViewResolver.class)).isEmpty();
  }

  @Test
  void registerCompilerForReactiveApp() {
    this.loadWithReactive(CustomPebbleEngineCompilerConfiguration.class);
    assertThat(this.reactiveWebContext.getBeansOfType(Loader.class)).hasSize(1);
    assertThat(this.reactiveWebContext.getBeansOfType(SpringExtension.class)).hasSize(1);
    assertThat(this.reactiveWebContext.getBeansOfType(PebbleEngine.class)).hasSize(1);
    assertThat(this.reactiveWebContext.getBean(PebbleEngine.class).getDefaultLocale()).isEqualTo(DEFAULT_LOCALE);
    assertThat(this.reactiveWebContext.getBeansOfType(PebbleReactiveViewResolver.class)).hasSize(1);
    assertThat(this.reactiveWebContext.getBeansOfType(PebbleViewResolver.class)).isEmpty();
  }

  private void loadWithServlet(Class<?> config) {
    this.webContext = new AnnotationConfigServletWebApplicationContext();
    TestPropertyValues.of("pebble.prefix=classpath:/templates/").applyTo(this.webContext);
    if (config != null) {
      this.webContext.register(config);
    }
    this.webContext.register(BaseConfiguration.class);
    this.webContext.refresh();
  }

  private void loadWithReactive(Class<?> config) {
    this.reactiveWebContext = new AnnotationConfigReactiveWebApplicationContext();
    TestPropertyValues.of("pebble.prefix=classpath:/templates/").applyTo(this.reactiveWebContext);
    if (config != null) {
      this.reactiveWebContext.register(config);
    }
    this.reactiveWebContext.register(BaseConfiguration.class);
    this.reactiveWebContext.refresh();
  }

  @Configuration(proxyBeanMethods = false)
  @Import(PebbleAutoConfiguration.class)
  protected static class BaseConfiguration {

  }

  @Configuration(proxyBeanMethods = false)
  protected static class CustomPebbleEngineCompilerConfiguration {

    @Bean
    public PebbleEngine pebbleEngine() {
      return new PebbleEngine.Builder().defaultLocale(DEFAULT_LOCALE).build();
    }

    @Bean
    public SpringExtension customSpringExtension() {
      return new SpringExtension();
    }
  }

}