package com.mitchellbosecke.pebble.cache.tag;

import com.mitchellbosecke.pebble.cache.CacheKey;
import com.mitchellbosecke.pebble.cache.PebbleCache;
import java.util.function.Function;

public class NoOpTagCache implements PebbleCache<CacheKey, Object> {

  @Override
  public Object computeIfAbsent(CacheKey key,
      Function<? super CacheKey, ?> mappingFunction) {
    return mappingFunction.apply(key);
  }

  @Override
  public void invalidateAll() {}
}
