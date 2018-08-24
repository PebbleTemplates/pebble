package com.mitchellbosecke.pebble.cache.template;

import com.mitchellbosecke.pebble.cache.PebbleCache;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.util.function.Function;

public class NoOpTemplateCache implements PebbleCache<Object, PebbleTemplate> {

  @Override
  public PebbleTemplate computeIfAbsent(Object key,
      Function<? super Object, ? extends PebbleTemplate> mappingFunction) {
    return mappingFunction.apply(key);
  }

  @Override
  public void invalidateAll() {}
}
