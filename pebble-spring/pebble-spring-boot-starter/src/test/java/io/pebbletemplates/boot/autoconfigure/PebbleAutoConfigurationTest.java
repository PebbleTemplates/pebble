package io.pebbletemplates.boot.autoconfigure;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.attributes.methodaccess.BlacklistMethodAccessValidator;
import io.pebbletemplates.pebble.attributes.methodaccess.MethodAccessValidator;
import io.pebbletemplates.pebble.attributes.methodaccess.NoOpMethodAccessValidator;
import io.pebbletemplates.pebble.loader.Loader;
import io.pebbletemplates.spring.extension.SpringExtension;
import io.pebbletemplates.spring.reactive.PebbleReactiveView;
import io.pebbletemplates.spring.reactive.PebbleReactiveViewResolver;
import io.pebbletemplates.spring.servlet.PebbleView;
import io.pebbletemplates.spring.servlet.PebbleViewResolver;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.context.reactive.AnnotationConfigReactiveWebApplicationContext;
import org.springframework.boot.web.context.servlet.AnnotationConfigServletWebApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.result.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;

import java.util.Locale;

import static java.util.Locale.CHINESE;
import static java.util.Locale.FRENCH;
import static org.assertj.core.api.Assertions.assertThat;

class PebbleAutoConfigurationTest {

  private static final Locale DEFAULT_LOCALE = CHINESE;
  private static final Locale CUSTOM_LOCALE = FRENCH;
  private AnnotationConfigServletWebApplicationContext webContext;

  private AnnotationConfigReactiveWebApplicationContext reactiveWebContext;

  @Test
  void registerBeansForServletApp() {
    this.loadWithServlet(null);
    assertThat(this.webContext.getBeansOfType(Loader.class)).hasSize(1);
    assertThat(this.webContext.getBeansOfType(SpringExtension.class)).hasSize(1);
    assertThat(this.webContext.getBeansOfType(PebbleEngine.class)).hasSize(1);
    assertThat(this.webContext.getBean(PebbleEngine.class).getDefaultLocale())
        .isEqualTo(DEFAULT_LOCALE);
    assertThat(this.webContext.getBean(PebbleEngine.class).isStrictVariables()).isTrue();
    assertThat(
        this.webContext.getBean(PebbleEngine.class).getEvaluationOptions().isGreedyMatchMethod())
        .isTrue();
    assertThat(this.webContext.getBean(PebbleEngine.class).getEvaluationOptions()
        .getMethodAccessValidator()).isInstanceOf(
        BlacklistMethodAccessValidator.class);
    assertThat(this.webContext.getBeansOfType(PebbleViewResolver.class)).hasSize(1);
  }

  @Test
  void registerCustomBeansForServletApp() {
    this.loadWithServlet(CustomPebbleViewResolverConfiguration.class);
    assertThat(this.webContext.getBeansOfType(PebbleViewResolver.class)).hasSize(0);
    assertThat(this.webContext.getBeansOfType(AbstractTemplateViewResolver.class)).hasSize(1);
    assertThat(this.webContext.getBeansOfType(CustomPebbleViewResolver.class)).hasSize(1);
  }

  @Test
  void registerCompilerForServletApp() {
    this.loadWithServlet(CustomPebbleEngineCompilerConfiguration.class);
    assertThat(this.webContext.getBeansOfType(Loader.class)).hasSize(1);
    assertThat(this.webContext.getBeansOfType(SpringExtension.class)).hasSize(1);
    assertThat(this.webContext.getBeansOfType(PebbleEngine.class)).hasSize(1);
    assertThat(this.webContext.getBean(PebbleEngine.class).getDefaultLocale())
        .isEqualTo(CUSTOM_LOCALE);
    assertThat(this.webContext.getBean(PebbleEngine.class).isStrictVariables()).isFalse();
    assertThat(
        this.webContext.getBean(PebbleEngine.class).getEvaluationOptions().isGreedyMatchMethod())
        .isFalse();
    assertThat(this.webContext.getBean(PebbleEngine.class).getEvaluationOptions()
        .getMethodAccessValidator()).isInstanceOf(
        BlacklistMethodAccessValidator.class);
    assertThat(this.webContext.getBeansOfType(PebbleViewResolver.class)).hasSize(1);
    assertThat(this.webContext.getBeansOfType(PebbleReactiveViewResolver.class)).isEmpty();
  }

  @Test
  void registerCustomMethodAccessValidatorForServletApp() {
    this.loadWithServlet(CustomMethodAccessValidatorConfiguration.class);
    assertThat(this.webContext.getBeansOfType(Loader.class)).hasSize(1);
    assertThat(this.webContext.getBeansOfType(SpringExtension.class)).hasSize(1);
    assertThat(this.webContext.getBeansOfType(PebbleEngine.class)).hasSize(1);
    assertThat(this.webContext.getBean(PebbleEngine.class).getDefaultLocale())
        .isEqualTo(DEFAULT_LOCALE);
    assertThat(this.webContext.getBean(PebbleEngine.class).isStrictVariables()).isTrue();
    assertThat(
        this.webContext.getBean(PebbleEngine.class).getEvaluationOptions().isGreedyMatchMethod())
        .isTrue();
    assertThat(this.webContext.getBean(PebbleEngine.class).getEvaluationOptions()
        .getMethodAccessValidator()).isInstanceOf(
        NoOpMethodAccessValidator.class);
    assertThat(this.webContext.getBeansOfType(PebbleViewResolver.class)).hasSize(1);
    assertThat(this.webContext.getBeansOfType(PebbleReactiveViewResolver.class)).isEmpty();
  }

  @Test
  void registerBeansForReactiveApp() {
    this.loadWithReactive(null);
    assertThat(this.reactiveWebContext.getBeansOfType(Loader.class)).hasSize(1);
    assertThat(this.reactiveWebContext.getBeansOfType(SpringExtension.class)).hasSize(1);
    assertThat(this.reactiveWebContext.getBeansOfType(PebbleEngine.class)).hasSize(1);
    assertThat(this.reactiveWebContext.getBean(PebbleEngine.class).getDefaultLocale())
        .isEqualTo(DEFAULT_LOCALE);
    assertThat(this.reactiveWebContext.getBean(PebbleEngine.class).isStrictVariables()).isTrue();
    assertThat(this.reactiveWebContext.getBean(PebbleEngine.class).getEvaluationOptions()
        .isGreedyMatchMethod()).isTrue();
    assertThat(this.reactiveWebContext.getBean(PebbleEngine.class).getEvaluationOptions()
        .getMethodAccessValidator()).isInstanceOf(
        BlacklistMethodAccessValidator.class);
    assertThat(this.reactiveWebContext.getBeansOfType(PebbleViewResolver.class)).isEmpty();
    assertThat(this.reactiveWebContext.getBeansOfType(PebbleReactiveViewResolver.class)).hasSize(1);
    assertThat(this.reactiveWebContext.getBeansOfType(PebbleViewResolver.class)).isEmpty();
  }

  @Test
  void registerCustomBeansForReactiveApp() {
    this.loadWithReactive(CustomPebbleReactiveViewResolverConfiguration.class);
    assertThat(this.reactiveWebContext.getBeansOfType(PebbleReactiveViewResolver.class)).hasSize(0);
    assertThat(this.reactiveWebContext.getBeansOfType(UrlBasedViewResolver.class)).hasSize(1);
    assertThat(this.reactiveWebContext.getBeansOfType(CustomPebbleReactiveViewResolver.class)).hasSize(1);
  }

  @Test
  void registerCompilerForReactiveApp() {
    this.loadWithReactive(CustomPebbleEngineCompilerConfiguration.class);
    assertThat(this.reactiveWebContext.getBeansOfType(Loader.class)).hasSize(1);
    assertThat(this.reactiveWebContext.getBeansOfType(SpringExtension.class)).hasSize(1);
    assertThat(this.reactiveWebContext.getBeansOfType(PebbleEngine.class)).hasSize(1);
    assertThat(this.reactiveWebContext.getBean(PebbleEngine.class).getDefaultLocale())
        .isEqualTo(CUSTOM_LOCALE);
    assertThat(this.reactiveWebContext.getBean(PebbleEngine.class).isStrictVariables()).isFalse();
    assertThat(this.reactiveWebContext.getBean(PebbleEngine.class).getEvaluationOptions()
        .isGreedyMatchMethod()).isFalse();
    assertThat(this.reactiveWebContext.getBean(PebbleEngine.class).getEvaluationOptions()
        .getMethodAccessValidator()).isInstanceOf(
        BlacklistMethodAccessValidator.class);
    assertThat(this.reactiveWebContext.getBeansOfType(PebbleReactiveViewResolver.class)).hasSize(1);
    assertThat(this.reactiveWebContext.getBeansOfType(PebbleViewResolver.class)).isEmpty();
  }

  @Test
  void registerCustomMethodAccessValidatorForReactiveApp() {
    this.loadWithReactive(CustomMethodAccessValidatorConfiguration.class);
    assertThat(this.reactiveWebContext.getBeansOfType(Loader.class)).hasSize(1);
    assertThat(this.reactiveWebContext.getBeansOfType(SpringExtension.class)).hasSize(1);
    assertThat(this.reactiveWebContext.getBeansOfType(PebbleEngine.class)).hasSize(1);
    assertThat(this.reactiveWebContext.getBean(PebbleEngine.class).getDefaultLocale())
        .isEqualTo(DEFAULT_LOCALE);
    assertThat(this.reactiveWebContext.getBean(PebbleEngine.class).isStrictVariables()).isTrue();
    assertThat(this.reactiveWebContext.getBean(PebbleEngine.class).getEvaluationOptions()
        .isGreedyMatchMethod()).isTrue();
    assertThat(this.reactiveWebContext.getBean(PebbleEngine.class).getEvaluationOptions()
        .getMethodAccessValidator()).isInstanceOf(
        NoOpMethodAccessValidator.class);
    assertThat(this.reactiveWebContext.getBeansOfType(PebbleViewResolver.class)).isEmpty();
    assertThat(this.reactiveWebContext.getBeansOfType(PebbleReactiveViewResolver.class)).hasSize(1);
    assertThat(this.reactiveWebContext.getBeansOfType(PebbleViewResolver.class)).isEmpty();
  }

  private void loadWithServlet(Class<?> config) {
    this.webContext = new AnnotationConfigServletWebApplicationContext();
      TestPropertyValues.of("spring.pebble.prefix=classpath:/templates/").applyTo(this.webContext);
      TestPropertyValues.of("spring.pebble.defaultLocale=zh").applyTo(this.webContext);
      TestPropertyValues.of("spring.pebble.strictVariables=true").applyTo(this.webContext);
      TestPropertyValues.of("spring.pebble.greedyMatchMethod=true").applyTo(this.webContext);
    if (config != null) {
      this.webContext.register(config);
    }
    this.webContext.register(BaseConfiguration.class);
    this.webContext.refresh();
  }

  private void loadWithReactive(Class<?> config) {
    this.reactiveWebContext = new AnnotationConfigReactiveWebApplicationContext();
      TestPropertyValues.of("spring.pebble.prefix=classpath:/templates/").applyTo(this.reactiveWebContext);
      TestPropertyValues.of("spring.pebble.defaultLocale=zh").applyTo(this.reactiveWebContext);
      TestPropertyValues.of("spring.pebble.strictVariables=true").applyTo(this.reactiveWebContext);
      TestPropertyValues.of("spring.pebble.greedyMatchMethod=true").applyTo(this.reactiveWebContext);
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
      return new PebbleEngine.Builder().defaultLocale(CUSTOM_LOCALE).build();
    }

    @Bean
    public SpringExtension customSpringExtension(MessageSource messageSource) {
      return new SpringExtension(messageSource);
    }

  }

  @Configuration(proxyBeanMethods = false)
  protected static class CustomPebbleViewResolverConfiguration {

    @Bean
    public CustomPebbleViewResolver pebbleViewResolver() {
      return new CustomPebbleViewResolver();
    }

  }

  @Configuration(proxyBeanMethods = false)
  protected static class CustomPebbleReactiveViewResolverConfiguration {

    @Bean
    public CustomPebbleReactiveViewResolver pebbleReactiveViewResolver() {
      return new CustomPebbleReactiveViewResolver();
    }

  }

  protected static class CustomPebbleViewResolver extends AbstractTemplateViewResolver {

    public CustomPebbleViewResolver() {
        this.setViewClass(PebbleView.class);
    }

  }

  protected static class CustomPebbleReactiveViewResolver extends UrlBasedViewResolver {

      public CustomPebbleReactiveViewResolver() {
          this.setViewClass(PebbleReactiveView.class);
      }

    }

  @Configuration(proxyBeanMethods = false)
  protected static class CustomMethodAccessValidatorConfiguration {

    @Bean
    public MethodAccessValidator methodAccessValidator() {
      return new NoOpMethodAccessValidator();
    }
  }

}