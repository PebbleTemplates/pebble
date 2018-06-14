package com.mitchellbosecke.pebble.boot;

import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@SpringBootApplication
@ServletComponentScan
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public LocaleResolver localeResolver() {
    return new AcceptHeaderLocaleResolver();
  }

  @Bean
  public MessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename("messages");
    messageSource.setFallbackToSystemLocale(false);
    return messageSource;
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