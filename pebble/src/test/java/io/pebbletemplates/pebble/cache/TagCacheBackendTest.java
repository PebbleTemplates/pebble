package io.pebbletemplates.pebble.cache;

import io.pebbletemplates.pebble.cache.tag.CaffeineTagCache;
import io.pebbletemplates.pebble.cache.tag.ConcurrentMapTagCache;
import io.pebbletemplates.pebble.cache.tag.NoOpTagCache;
import io.pebbletemplates.pebble.node.CacheNode;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TagCacheBackendTest {

  @Test
  void cachesTagValuesUntilInvalidated() {
    assertCacheBehavior(new ConcurrentMapTagCache(), true);
    assertCacheBehavior(new CaffeineTagCache(), true);
  }

  @Test
  void noOpCacheRecomputesOnEveryLookup() {
    assertCacheBehavior(new NoOpTagCache(), false);
  }

  private void assertCacheBehavior(PebbleCache<CacheKey, Object> cache,
      boolean cachesValues) {
    CacheKey key = new CacheKey(new CacheNode(1, null, null), "key", Locale.ROOT);
    AtomicInteger computations = new AtomicInteger();

    cache.computeIfAbsent(key, ignored -> {
      computations.incrementAndGet();
      return "value";
    });
    cache.computeIfAbsent(key, ignored -> {
      computations.incrementAndGet();
      return "value";
    });
    assertEquals(cachesValues ? 1 : 2, computations.get());

    cache.invalidateAll();
    cache.computeIfAbsent(key, ignored -> {
      computations.incrementAndGet();
      return "value";
    });
    assertEquals(cachesValues ? 2 : 3, computations.get());
  }
}
