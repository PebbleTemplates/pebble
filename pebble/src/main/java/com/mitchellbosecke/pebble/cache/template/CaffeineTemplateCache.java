package com.mitchellbosecke.pebble.cache.template;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mitchellbosecke.pebble.cache.PebbleCache;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.util.function.Function;

public class CaffeineTemplateCache implements PebbleCache<Object, PebbleTemplate> {

  private final Cache<Object, PebbleTemplate> templateCache;

  public CaffeineTemplateCache() {
    this.templateCache = Caffeine.newBuilder()
        .maximumSize(200)
        .build();
  }

  public CaffeineTemplateCache(Cache<Object, PebbleTemplate> templateCache) {
    this.templateCache = templateCache;
  }

  @Override
  public PebbleTemplate computeIfAbsent(Object key,
      Function<? super Object, ? extends PebbleTemplate> mappingFunction) {
    return this.templateCache.get(key, mappingFunction);
  }

  @Override
  public void invalidateAll() {
    this.templateCache.invalidateAll();
  }
}

