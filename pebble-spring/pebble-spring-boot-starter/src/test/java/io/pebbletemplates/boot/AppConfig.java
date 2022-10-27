package io.pebbletemplates.boot;

import io.pebbletemplates.pebble.extension.AbstractExtension;
import io.pebbletemplates.pebble.extension.Extension;
import io.pebbletemplates.pebble.extension.Function;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
public class AppConfig {
  @Bean
  public MessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename("messages");
    messageSource.setFallbackToSystemLocale(false);
    return messageSource;
  }

  @Bean
  public LocaleResolver localeResolver() {
    return new AcceptHeaderLocaleResolver();
  }

  @Bean
  public Extension testExtension() {
    return new TestExtension();
  }

  public static class TestExtension extends AbstractExtension {

    @Override
    public Map<String, Function> getFunctions() {
      Map<String, Function> functions = new HashMap<String, Function>();
      functions.put("testFunction", new TestFunction());
      return functions;
    }

    public static class TestFunction implements Function {

      @Override
      public List<String> getArgumentNames() {
        return Collections.emptyList();
      }

      @Override
      public Object execute(Map<String, Object> args, PebbleTemplate self,
                            EvaluationContext context, int lineNumber) {
        return "Tested!";
      }
    }
  }
}
