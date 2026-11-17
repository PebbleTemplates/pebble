package io.pebbletemplates.pebble.cache;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.cache.template.CaffeineTemplateCache;
import io.pebbletemplates.pebble.cache.template.ConcurrentMapTemplateCache;
import io.pebbletemplates.pebble.cache.template.NoOpTemplateCache;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TemplateCacheBackendTest {

  @Test
  void cachesTemplatesUntilInvalidated() {
    assertCacheBehavior(new ConcurrentMapTemplateCache(), true);
    assertCacheBehavior(new CaffeineTemplateCache(), true);
  }

  @Test
  void noOpCacheRecomputesOnEveryLookup() {
    assertCacheBehavior(new NoOpTemplateCache(), false);
  }

  private void assertCacheBehavior(PebbleCache<Object, PebbleTemplate> cache,
      boolean cachesValues) {
    PebbleTemplate template = new PebbleEngine.Builder().build().getLiteralTemplate("value");
    AtomicInteger computations = new AtomicInteger();

    cache.computeIfAbsent("key", key -> {
      computations.incrementAndGet();
      return template;
    });
    cache.computeIfAbsent("key", key -> {
      computations.incrementAndGet();
      return template;
    });
    assertEquals(cachesValues ? 1 : 2, computations.get());

    cache.invalidateAll();
    cache.computeIfAbsent("key", key -> {
      computations.incrementAndGet();
      return template;
    });
    assertEquals(cachesValues ? 2 : 3, computations.get());
  }
}
