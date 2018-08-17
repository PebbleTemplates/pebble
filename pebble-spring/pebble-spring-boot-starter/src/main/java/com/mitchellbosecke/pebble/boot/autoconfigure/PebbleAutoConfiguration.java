package com.mitchellbosecke.pebble.boot.autoconfigure;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.spring4.PebbleViewResolver;
import com.mitchellbosecke.pebble.spring4.extension.SpringExtension;
import java.util.List;
import javax.servlet.Servlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(PebbleEngine.class)
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
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
  @ConditionalOnWebApplication
  @ConditionalOnClass({Servlet.class})
  public static class PebbleViewResolverConfiguration {

    @Autowired
    private PebbleProperties properties;

    @Autowired
    private PebbleEngine pebbleEngine;

    @Bean
    @ConditionalOnMissingBean(name = "pebbleViewResolver")
    public PebbleViewResolver pebbleViewResolver() {
      PebbleViewResolver pvr = new PebbleViewResolver();
      this.properties.applyToViewResolver(pvr);

      pvr.setPebbleEngine(this.pebbleEngine);
      if (this.pebbleEngine.getLoader() instanceof ClasspathLoader) {
        // classpathloader doesn't like leading slashes in paths
        pvr.setPrefix(stripLeadingSlash(this.properties.getPrefix()));
      }

      return pvr;
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
