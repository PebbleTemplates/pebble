/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.error.RootAttributeNotFoundException;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContextTest {

  @SuppressWarnings("serial")
  @Test
  void testLazyMap() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble.getTemplate("{{ eager_key }} {{ lazy_key }}");
    Writer writer = new StringWriter();
    template.evaluate(writer, new HashMap<String, Object>() {

      {
        this.put("eager_key", "eager_value");
      }

      @Override
      public Object get(final Object key) {
        if ("lazy_key".equals(key)) {
          return "lazy_value";
        }
        return super.get(key);
      }

      @Override
      public boolean containsKey(Object key) {
        if ("lazy_key".equals(key)) {
          return true;
        }
        return super.containsKey(key);
      }
    });
    assertEquals("eager_value lazy_value", writer.toString());
  }

  @Test
  void testMissingContextVariableWithoutStrictVariables()
      throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();

    PebbleTemplate template = pebble.getTemplate("{{ foo }}");
    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("", writer.toString());
  }

  @Test
  void testMissingContextVariableWithStrictVariables() throws PebbleException, IOException {
    assertThrows(RootAttributeNotFoundException.class, () -> {
      PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
          .strictVariables(true).build();

      PebbleTemplate template = pebble.getTemplate("{{ foo }}");
      Writer writer = new StringWriter();
      template.evaluate(writer);
    });
  }

  @Test
  void testExistingButNullContextVariableWithStrictVariables()
      throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(true).build();

    PebbleTemplate template = pebble.getTemplate("{% if foo == null %}YES{% endif %}");

    Map<String, Object> context = new HashMap<>();
    context.put("foo", null);

    Writer writer = new StringWriter();
    template.evaluate(writer, context);
    assertEquals("YES", writer.toString());
  }

  @Test
  void testDefaultLocale() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false)
        .defaultLocale(Locale.CANADA_FRENCH).build();
    PebbleTemplate template = pebble.getTemplate("{{ locale.language }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("fr", writer.toString());
  }

  @Test
  void testLocaleProvidedDuringEvaluation() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false)
        .defaultLocale(Locale.CANADA).build();
    PebbleTemplate template = pebble.getTemplate("{{ locale }}");

    Writer writer = new StringWriter();
    template.evaluate(writer, Locale.CANADA);
    assertEquals("en_CA", writer.toString());
  }

  @Test
  void testGlobalTemplateName() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine.Builder().loader(new StringLoader())
        .strictVariables(false).build();
    PebbleTemplate template = pebble.getTemplate("{{ template.name }}");

    Writer writer = new StringWriter();
    template.evaluate(writer);
    assertEquals("{{ template.name }}", writer.toString());
  }

  @Test
  void testImmutableMapThrows() throws PebbleException, IOException {
    assertThrows(UnsupportedOperationException.class, () -> {
      Map<String, Object> originalMap = new HashMap<>();
      originalMap.put("contextVariable", "context variable value");
      Map<String, Object> immutableMap = new ImmutableMap<>(originalMap);

      immutableMap.put("templateVariable", "template variable value");
    });
  }

  @Test
  void testImmutableContext() throws PebbleException, IOException {
    PebbleEngine pebble = new PebbleEngine
        .Builder()
        .loader(new StringLoader())
        .strictVariables(false)
        .build();
    PebbleTemplate template = pebble.getTemplate("" +
        "{% set templateVariable = 'template variable value' %}" +
        "{{ contextVariable }} - {{ templateVariable }}");

    Writer writer = new StringWriter();

    Map<String, Object> originalMap = new HashMap<>();
    originalMap.put("contextVariable", "context variable value");

    template.evaluate(writer, new ImmutableMap<>(originalMap));
    assertEquals("context variable value - template variable value", writer.toString());
  }

  private static class ImmutableMap<T, U> implements Map<T, U> {

    private final Map<T, U> delegate;

    private ImmutableMap(Map<T, U> delegate) {
      this.delegate = delegate;
    }

    @Override
    public int size() {
      return this.delegate.size();
    }

    @Override
    public boolean isEmpty() {
      return this.delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
      return this.delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
      return this.delegate.containsValue(value);
    }

    @Override
    public U get(Object key) {
      return this.delegate.get(key);
    }

    @Override
    public U put(T key, U value) {
      throw new UnsupportedOperationException("this map is immutable");
    }

    @Override
    public U remove(Object key) {
      throw new UnsupportedOperationException("this map is immutable");
    }

    @Override
    public void putAll(Map<? extends T, ? extends U> m) {
      throw new UnsupportedOperationException("this map is immutable");
    }

    @Override
    public void clear() {
      throw new UnsupportedOperationException("this map is immutable");
    }

    @Override
    public Set<T> keySet() {
      return this.delegate.keySet();
    }

    @Override
    public Collection<U> values() {
      return this.delegate.values();
    }

    @Override
    public Set<Entry<T, U>> entrySet() {
      return this.delegate.entrySet();
    }

    @Override
    public boolean equals(Object o) {
      return this.delegate.equals(o);
    }

    @Override
    public int hashCode() {
      return this.delegate.hashCode();
    }

    @Override
    public U getOrDefault(Object key, U defaultValue) {
      return this.delegate.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super T, ? super U> action) {
      this.delegate.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super T, ? super U, ? extends U> function) {
      throw new UnsupportedOperationException("this map is immutable");
    }

    @Override
    public U putIfAbsent(T key, U value) {
      throw new UnsupportedOperationException("this map is immutable");
    }

    @Override
    public boolean remove(Object key, Object value) {
      throw new UnsupportedOperationException("this map is immutable");
    }

    @Override
    public boolean replace(T key, U oldValue, U newValue) {
      throw new UnsupportedOperationException("this map is immutable");
    }

    @Override
    public U replace(T key, U value) {
      throw new UnsupportedOperationException("this map is immutable");
    }

    @Override
    public U computeIfAbsent(T key, Function<? super T, ? extends U> mappingFunction) {
      throw new UnsupportedOperationException("this map is immutable");
    }

    @Override
    public U computeIfPresent(T key, BiFunction<? super T, ? super U, ? extends U> remappingFunction) {
      throw new UnsupportedOperationException("this map is immutable");
    }

    @Override
    public U compute(T key, BiFunction<? super T, ? super U, ? extends U> remappingFunction) {
      throw new UnsupportedOperationException("this map is immutable");
    }

    @Override
    public U merge(T key, U value, BiFunction<? super U, ? super U, ? extends U> remappingFunction) {
      throw new UnsupportedOperationException("this map is immutable");
    }
  }

}
