package com.mitchellbosecke.pebble.cache;

import java.util.function.Function;

public interface PebbleCache<K, V> {

  V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction);

  void invalidateAll();
}
