package com.mitchellbosecke.pebble.boot.autoconfigure;

import java.util.Locale;
import org.springframework.boot.autoconfigure.template.AbstractTemplateViewResolverProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("pebble")
public class PebbleProperties extends AbstractTemplateViewResolverProperties {

  public static final String DEFAULT_PREFIX = "/templates/";
  public static final String DEFAULT_SUFFIX = ".pebble";

  private Locale defaultLocale;
  private boolean strictVariables;
  private boolean greedyMatchMethod;

  public PebbleProperties() {
    super(DEFAULT_PREFIX, DEFAULT_SUFFIX);
    this.setCache(true);
  }

  public Locale getDefaultLocale() {
    return this.defaultLocale;
  }

  public void setDefaultLocale(Locale defaultLocale) {
    this.defaultLocale = defaultLocale;
  }

  public boolean isStrictVariables() {
    return this.strictVariables;
  }

  public void setStrictVariables(boolean strictVariables) {
    this.strictVariables = strictVariables;
  }

  public boolean isGreedyMatchMethod() {
    return this.greedyMatchMethod;
  }

  public void setGreedyMatchMethod(boolean greedyMatchMethod) {
    this.greedyMatchMethod = greedyMatchMethod;
  }
}
