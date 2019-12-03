package com.mitchellbosecke.pebble.boot.autoconfigure;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.spring.extension.SpringExtension;
import com.mitchellbosecke.pebble.spring.reactive.PebbleReactiveViewResolver;
import com.mitchellbosecke.pebble.spring.servlet.PebbleViewResolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConditionalOnClass(PebbleEngine.class)
@AutoConfigureAfter({WebMvcAutoConfiguration.class, WebFluxAutoConfiguration.class})
@EnableConfigurationProperties(PebbleProperties.class)
public class PebbleAutoConfiguration {

  @Configuration
  @ConditionalOnMissingBean(name = "pebbleLoader")
  public static class DefaultLoaderConfiguration {

    @Autowired
    private PebbleProperties properties;

    @Bean
    public Loader<?> pebbleLoader() {
      ClasspathLoader loader = new ClasspathLoader();
      loader.setCharset(this.properties.getCharsetName());
      // classpath loader does not like leading slashes in resource paths
      loader.setPrefix(stripLeadingSlash(this.properties.getPrefix()));
      loader.setSuffix(this.properties.getSuffix());
      return loader;
    }
  }

  @Configuration
  @ConditionalOnMissingBean(name = "pebbleEngine")
  public static class PebbleDefaultConfiguration {

    @Autowired
    private PebbleProperties properties;

    @Autowired
    private Loader<?> pebbleLoader;

    @Autowired(required = false)
    private List<Extension> extensions;

    @Bean
    public SpringExtension pebbleSpringExtension() {
      return new SpringExtension();
    }

    @Bean
    public PebbleEngine pebbleEngine() {
      PebbleEngine.Builder builder = new PebbleEngine.Builder();
      builder.loader(this.pebbleLoader);
      builder.extension(this.pebbleSpringExtension());
      if (this.extensions != null && !this.extensions.isEmpty()) {
        builder.extension(this.extensions.toArray(new Extension[this.extensions.size()]));
      }
      if (!this.properties.isCache()) {
        builder.cacheActive(false);
      }
      if (this.properties.getDefaultLocale() != null) {
        builder.defaultLocale(this.properties.getDefaultLocale());
      }
      builder.strictVariables(this.properties.isStrictVariables());
      builder.greedyMatchMethod(this.properties.isGreedyMatchMethod());
      return builder.build();
    }
  }

  @Configuration
  @ConditionalOnWebApplication(type = Type.SERVLET)
  public static class PebbleWebMvcConfiguration {

    @Autowired
    private PebbleProperties properties;

    @Autowired
    private PebbleEngine pebbleEngine;

    @Bean
    @ConditionalOnMissingBean(name = "pebbleViewResolver")
    public PebbleViewResolver pebbleViewResolver() {
      PebbleViewResolver pvr = new PebbleViewResolver();
      this.properties.applyToMvcViewResolver(pvr);

      pvr.setPebbleEngine(this.pebbleEngine);
      if (this.pebbleEngine.getLoader() instanceof ClasspathLoader) {
        // classpathloader doesn't like leading slashes in paths
        pvr.setPrefix(stripLeadingSlash(this.properties.getPrefix()));
      }

      return pvr;
    }
  }

  @Configuration
  @ConditionalOnWebApplication(type = Type.REACTIVE)
  public static class PebbleReactiveConfiguration {

    @Autowired
    private PebbleProperties properties;

    @Autowired
    private PebbleEngine pebbleEngine;

    @Bean
    @ConditionalOnMissingBean(name = "pebbleReactiveViewResolver")
    public PebbleReactiveViewResolver pebbleReactiveViewResolver() {
      String prefix = this.properties.getPrefix();
      if (this.pebbleEngine.getLoader() instanceof ClasspathLoader) {
        // classpathloader doesn't like leading slashes in paths
        prefix = stripLeadingSlash(this.properties.getPrefix());
      }
      PebbleReactiveViewResolver resolver = new PebbleReactiveViewResolver(this.pebbleEngine);
      resolver.setPrefix(prefix);
      resolver.setSuffix(this.properties.getSuffix());
      resolver.setViewNames(this.properties.getViewNames());
      resolver.setRequestContextAttribute(this.properties.getRequestContextAttribute());
      return resolver;
    }
  }

  private static String stripLeadingSlash(String value) {
    if (value == null) {
      return null;
    }
    if (value.startsWith("/")) {
      return value.substring(1);
    }
    return value;
  }

}
