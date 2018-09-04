package com.mitchellbosecke.pebble.cache.tag;

import com.mitchellbosecke.pebble.cache.CacheKey;
import com.mitchellbosecke.pebble.cache.PebbleCache;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public class ConcurrentMapTagCache implements PebbleCache<CacheKey, Object> {

  private final ConcurrentMap<CacheKey, Object> tagCache;

  public ConcurrentMapTagCache() {
    this.tagCache = new ConcurrentHashMap<>(200);
  }

  public ConcurrentMapTagCache(ConcurrentMap<CacheKey, Object> tagCache) {
    this.tagCache = tagCache;
  }

  @Override
  public Object computeIfAbsent(CacheKey key,
      Function<? super CacheKey, ?> mappingFunction) {
    return this.tagCache.computeIfAbsent(key, mappingFunction);
  }

  @Override
  public void invalidateAll() {
    this.tagCache.clear();
  }
}
