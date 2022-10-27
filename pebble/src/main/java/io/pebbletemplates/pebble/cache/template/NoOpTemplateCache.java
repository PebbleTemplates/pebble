package io.pebbletemplates.pebble.cache.template;

import io.pebbletemplates.pebble.cache.PebbleCache;
import io.pebbletemplates.pebble.template.PebbleTemplate;

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
