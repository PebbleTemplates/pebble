/*
 * Copyright (c) 2013 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.spring4.context;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

/**
 * <p>
 * Special object made available to templates in Spring MVC applications in order to access beans in
 * the Application Context.
 * </p>
 *
 * @author Eric Bussieres
 */
public class Beans implements Map<String, Object> {

  private final ApplicationContext ctx;

  public Beans(ApplicationContext ctx) {
    Assert.notNull(ctx, "Application Context cannot be null");
    this.ctx = ctx;
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException("Method \"clear\" not supported in Beans object");
  }

  @Override
  public boolean containsKey(Object key) {
    Assert.notNull(key, "Key cannot be null");
    return this.ctx.containsBean(key.toString());
  }

  @Override
  public boolean containsValue(Object value) {
    throw new UnsupportedOperationException(
        "Method \"containsValue\" not supported in Beans object");
  }

  @Override
  public Set<java.util.Map.Entry<String, Object>> entrySet() {
    throw new UnsupportedOperationException("Method \"entrySet\" not supported in Beans object");
  }

  @Override
  public Object get(Object key) {
    Assert.notNull(key, "Key cannot be null");
    return this.ctx.getBean(key.toString());
  }

  @Override
  public boolean isEmpty() {
    return this.size() <= 0;
  }

  @Override
  public Set<String> keySet() {
    return new LinkedHashSet<>(Arrays.asList(this.ctx.getBeanDefinitionNames()));
  }

  @Override
  public Object put(String key, Object value) {
    throw new UnsupportedOperationException("Method \"put\" not supported in Beans object");
  }

  @Override
  public void putAll(Map<? extends String, ?> m) {
    throw new UnsupportedOperationException("Method \"putAll\" not supported in Beans object");
  }

  @Override
  public Object remove(Object key) {
    throw new UnsupportedOperationException("Method \"remove\" not supported in Beans object");
  }

  @Override
  public int size() {
    return this.ctx.getBeanDefinitionCount();
  }

  @Override
  public Collection<Object> values() {
    throw new UnsupportedOperationException("Method \"values\" not supported in Beans object");
  }

}
