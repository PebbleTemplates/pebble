package com.mitchellbosecke.pebble.boot.autoconfigure;

import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;

public class PebbleTemplateAvailabilityProvider implements TemplateAvailabilityProvider {

  @Override
  public boolean isTemplateAvailable(String view, Environment environment, ClassLoader classLoader,
      ResourceLoader resourceLoader) {
    if (ClassUtils.isPresent("com.mitchellbosecke.pebble.PebbleEngine", classLoader)) {
      PropertyResolver resolver = new RelaxedPropertyResolver(environment, "pebble.");
      String prefix = resolver.getProperty("prefix", PebbleProperties.DEFAULT_PREFIX);
      String suffix = resolver.getProperty("suffix", PebbleProperties.DEFAULT_SUFFIX);
      return resourceLoader.getResource(prefix + view + suffix).exists();
    }
    return false;
  }

}
