/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.template;

import java.util.HashMap;
import java.util.Map;

/**
 * A scope is a map of variables. A "local" scope ensures that the search for a particular variable
 * will end at this scope whether or not it was found.
 *
 * @author Mitchell
 */
public class ScopeImpl implements Scope {

  /**
   * A "local" scope ensures that the search for a particular variable will end at this scope
   * whether or not it was found.
   */
  private final boolean local;

  /**
   * The map of variables known at this scope
   */
  private final Map<String, Object> backingMap;

  /**
   * Constructor
   *
   * @param backingMap The backing map of variables
   * @param local Whether this scope is local or not
   */
  public ScopeImpl(Map<String, Object> backingMap, boolean local) {
    this.backingMap = backingMap == null ? new HashMap<>() : backingMap;
    this.local = local;
  }

  @Override
  public Scope shallowCopy() {
    Map<String, Object> backingMapCopy = new HashMap<>(this.backingMap);
    return new ScopeImpl(backingMapCopy, this.local);
  }

  @Override
  public void put(String key, Object value) {
    this.backingMap.put(key, value);
  }

  @Override
  public Object get(String key) {
    return this.backingMap.get(key);
  }

  @Override
  public boolean containsKey(String key) {
    return this.backingMap.containsKey(key);
  }

  @Override
  public boolean isLocal() {
    return this.local;
  }

  @Override
  public boolean isWritable() {
    return false;
  }
}
