package io.pebbletemplates.pebble.cache.tag;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.pebbletemplates.pebble.cache.CacheKey;
import io.pebbletemplates.pebble.cache.PebbleCache;

import java.util.function.Function;

public class CaffeineTagCache implements PebbleCache<CacheKey, Object> {

  private final Cache<CacheKey, Object> tagCache;

  public CaffeineTagCache() {
    this.tagCache = Caffeine.newBuilder()
        .maximumSize(200)
        .build();
  }

  public CaffeineTagCache(Cache<CacheKey, Object> tagCache) {
    this.tagCache = tagCache;
  }

  @Override
  public Object computeIfAbsent(CacheKey key, Function<? super CacheKey, ?> mappingFunction) {
    return this.tagCache.get(key, mappingFunction);
  }

  @Override
  public void invalidateAll() {
    this.tagCache.invalidateAll();
  }
}

