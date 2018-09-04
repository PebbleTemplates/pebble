package com.mitchellbosecke.pebble.cache.template;

import com.mitchellbosecke.pebble.cache.PebbleCache;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public class ConcurrentMapTemplateCache implements PebbleCache<Object, PebbleTemplate> {

  private final ConcurrentMap<Object, PebbleTemplate> templateCache;

  public ConcurrentMapTemplateCache() {
    this.templateCache = new ConcurrentHashMap<>(200);
  }

  public ConcurrentMapTemplateCache(ConcurrentMap<Object, PebbleTemplate> templateCache) {
    this.templateCache = templateCache;
  }

  @Override
  public PebbleTemplate computeIfAbsent(Object key,
      Function<? super Object, ? extends PebbleTemplate> mappingFunction) {
    return this.templateCache.computeIfAbsent(key, mappingFunction);
  }

  @Override
  public void invalidateAll() {
    this.templateCache.clear();
  }
}
