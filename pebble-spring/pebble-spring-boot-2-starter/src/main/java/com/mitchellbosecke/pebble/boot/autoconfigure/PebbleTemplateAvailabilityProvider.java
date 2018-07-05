package com.mitchellbosecke.pebble.boot.autoconfigure;

import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;

public class PebbleTemplateAvailabilityProvider implements TemplateAvailabilityProvider {

  @Override
  public boolean isTemplateAvailable(String view, Environment environment, ClassLoader classLoader,
      ResourceLoader resourceLoader) {
    if (ClassUtils.isPresent("com.mitchellbosecke.pebble.PebbleEngine", classLoader)) {
      String prefix = environment.getProperty("pebble.prefix", PebbleProperties.DEFAULT_PREFIX);
      String suffix = environment.getProperty("pebble.suffix", PebbleProperties.DEFAULT_SUFFIX);
      return resourceLoader.getResource(prefix + view + suffix).exists();
    }
    return false;
  }

}
