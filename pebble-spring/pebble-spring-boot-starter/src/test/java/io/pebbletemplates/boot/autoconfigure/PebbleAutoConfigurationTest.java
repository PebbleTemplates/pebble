package io.pebbletemplates.boot.autoconfigure;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.attributes.methodaccess.AllowlistMethodAccessValidator;
import io.pebbletemplates.pebble.attributes.methodaccess.MethodAccessValidator;
import io.pebbletemplates.pebble.attributes.methodaccess.NoOpMethodAccessValidator;
import io.pebbletemplates.pebble.cache.tag.NoOpTagCache;
import io.pebbletemplates.pebble.cache.template.NoOpTemplateCache;
import io.pebbletemplates.pebble.loader.Loader;
import io.pebbletemplates.spring.extension.SpringExtension;
import io.pebbletemplates.spring.reactive.PebbleReactiveView;
import io.pebbletemplates.spring.reactive.PebbleReactiveViewResolver;
import io.pebbletemplates.spring.servlet.PebbleView;
import io.pebbletemplates.spring.servlet.PebbleViewResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.context.reactive.AnnotationConfigReactiveWebApplicationContext;
import org.springframework.boot.web.context.servlet.AnnotationConfigServletWebApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.server.MockWebSession;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.WebSession;
import org.springframework.web.reactive.result.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;

import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

import static java.util.Locale.CHINESE;
import static java.util.Locale.FRENCH;
import static org.assertj.core.api.Assertions.assertThat;

class PebbleAutoConfigurationTest {

  private static final Locale DEFAULT_LOCALE = CHINESE;
  private static final Locale CUSTOM_LOCALE = FRENCH;
  private AnnotationConfigServletWebApplicationContext webContext;
  private AnnotationConfigReactiveWebApplicationContext reactiveWebContext;

  @Test
  void servletDefaultAllowsSelectedMethodsOnly() throws Exception {
    this.loadWithServlet(null);
    PebbleEngine engine = this.webContext.getBean(PebbleEngine.class);
    MethodAccessValidator validator = engine.getEvaluationOptions().getMethodAccessValidator();
    assertThat(validator.isMethodAccessAllowed(new MockHttpServletRequest(),
        HttpServletRequest.class.getMethod("getContextPath"))).isTrue();
    assertThat(validator.isMethodAccessAllowed(new MockHttpServletResponse(),
        HttpServletResponse.class.getMethod("getStatus"))).isTrue();
    assertThat(validator.isMethodAccessAllowed(new MockHttpSession(),
        HttpSession.class.getMethod("getMaxInactiveInterval"))).isTrue();
    assertThat(validator.isMethodAccessAllowed(new MockHttpSession(),
        HttpSession.class.getMethod("invalidate"))).isFalse();
    assertThat(validator.isMethodAccessAllowed(new MockHttpServletResponse(),
        HttpServletResponse.class.getMethod("setStatus", int.class))).isFalse();
    assertThat(validator.isMethodAccessAllowed(new ProcessBuilder("echo"),
        ProcessBuilder.class.getMethod("start"))).isFalse();
    MockHttpSession session = new MockHttpSession();
    session.setMaxInactiveInterval(120);
    StringWriter output = new StringWriter();
    engine.getLiteralTemplate("{{ session.maxInactiveInterval }}")
        .evaluate(output, Map.of("session", session));
    assertThat(output.toString()).isEqualTo("120");
  }

  @Test
  void reactiveDefaultAllowsSelectedMethodsOnly() throws Exception {
    this.loadWithReactive(null);
    MethodAccessValidator validator =
        this.reactiveWebContext.getBean(PebbleEngine.class).getEvaluationOptions()
            .getMethodAccessValidator();
    assertThat(validator.isMethodAccessAllowed(MockServerHttpRequest.get("/").build(),
        ServerHttpRequest.class.getMethod("getPath"))).isTrue();
    assertThat(validator.isMethodAccessAllowed(new MockServerHttpResponse(),
        ServerHttpResponse.class.getMethod("getStatusCode"))).isTrue();
    assertThat(validator.isMethodAccessAllowed(new MockWebSession(),
        WebSession.class.getMethod("getAttributes"))).isTrue();
    assertThat(validator.isMethodAccessAllowed(new MockServerHttpResponse(),
        ServerHttpResponse.class.getMethod("setStatusCode", HttpStatusCode.class))).isFalse();
    assertThat(validator.isMethodAccessAllowed(new MockWebSession(),
        WebSession.class.getMethod("invalidate"))).isFalse();
    assertThat(validator.isMethodAccessAllowed(new ProcessBuilder("echo"),
        ProcessBuilder.class.getMethod("start"))).isFalse();
  }

  @Test
  void nonWebDefaultDoesNotRequireServletOrReactiveClasses() {
    new ApplicationContextRunner()
        .withClassLoader(new FilteredClassLoader("jakarta.servlet",
            "org.springframework.http.server.reactive", "org.springframework.web.reactive"))
        .withUserConfiguration(BaseConfiguration.class)
        .run(context -> {
          assertThat(context).hasNotFailed();
          assertThat(context.getBean(PebbleEngine.class).getEvaluationOptions()
              .getMethodAccessValidator()).isInstanceOf(AllowlistMethodAccessValidator.class);
        });
  }

  @Test
  void servletDefaultDoesNotRequireReactiveClasses() {
    new WebApplicationContextRunner()
        .withClassLoader(new FilteredClassLoader("org.springframework.http.server.reactive",
            "org.springframework.web.reactive"))
        .withUserConfiguration(BaseConfiguration.class)
        .run(context -> {
          assertThat(context).hasNotFailed();
          MethodAccessValidator validator =
              context.getBean(PebbleEngine.class).getEvaluationOptions().getMethodAccessValidator();
          assertThat(validator.isMethodAccessAllowed(new MockHttpServletRequest(),
              HttpServletRequest.class.getMethod("getContextPath"))).isTrue();
        });
  }

  @Test
  void reactiveDefaultDoesNotRequireServletClasses() {
    new ReactiveWebApplicationContextRunner()
        .withClassLoader(new FilteredClassLoader("jakarta.servlet"))
        .withUserConfiguration(BaseConfiguration.class)
        .run(context -> {
          assertThat(context).hasNotFailed();
          MethodAccessValidator validator =
              context.getBean(PebbleEngine.class).getEvaluationOptions().getMethodAccessValidator();
          assertThat(validator.isMethodAccessAllowed(MockServerHttpRequest.get("/").build(),
              ServerHttpRequest.class.getMethod("getPath"))).isTrue();
        });
  }

  @Test
  void registerBeansForServletApp() {
    this.loadWithServlet(null);
    assertThat(this.webContext.getBeansOfType(Loader.class)).hasSize(1);
    assertThat(this.webContext.getBeansOfType(SpringExtension.class)).hasSize(1);
    assertThat(this.webContext.getBeansOfType(PebbleEngine.class)).hasSize(1);
    assertThat(this.webContext.getBean(PebbleEngine.class).getDefaultLocale()).isEqualTo(DEFAULT_LOCALE);
    assertThat(this.webContext.getBean(PebbleEngine.class).isStrictVariables()).isTrue();
    assertThat(this.webContext.getBean(PebbleEngine.class).getEvaluationOptions().isGreedyMatchMethod()).isTrue();
    assertThat(this.webContext.getBean(PebbleEngine.class).getEvaluationOptions().getMethodAccessValidator()).isInstanceOf(AllowlistMethodAccessValidator.class);
    assertThat(this.webContext.getBean(PebbleEngine.class).getTagCache()).isInstanceOf(NoOpTagCache.class);
    assertThat(this.webContext.getBean(PebbleEngine.class).getTemplateCache()).isInstanceOf(NoOpTemplateCache.class);
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
            AllowlistMethodAccessValidator.class);
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
            AllowlistMethodAccessValidator.class);
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
            AllowlistMethodAccessValidator.class);
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
    TestPropertyValues.of("pebble.prefix=classpath:/templates/").applyTo(this.webContext);
    TestPropertyValues.of("pebble.defaultLocale=zh").applyTo(this.webContext);
    TestPropertyValues.of("pebble.strictVariables=true").applyTo(this.webContext);
    TestPropertyValues.of("pebble.greedyMatchMethod=true").applyTo(this.webContext);
    TestPropertyValues.of("pebble.servlet.cache=false").applyTo(this.webContext);
    if (config != null) {
      this.webContext.register(config);
    }
    this.webContext.register(BaseConfiguration.class);
    this.webContext.refresh();
  }

  private void loadWithReactive(Class<?> config) {
    this.reactiveWebContext = new AnnotationConfigReactiveWebApplicationContext();
    TestPropertyValues.of("pebble.prefix=classpath:/templates/").applyTo(this.reactiveWebContext);
    TestPropertyValues.of("pebble.defaultLocale=zh").applyTo(this.reactiveWebContext);
    TestPropertyValues.of("pebble.strictVariables=true").applyTo(this.reactiveWebContext);
    TestPropertyValues.of("pebble.greedyMatchMethod=true").applyTo(this.reactiveWebContext);
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
      return NoOpMethodAccessValidator.builder().build();
    }
  }

}