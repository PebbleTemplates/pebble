package io.pebbletemplates.cache.tag;

import io.pebbletemplates.cache.CacheKey;
import io.pebbletemplates.cache.PebbleCache;

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
