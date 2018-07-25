package com.mitchellbosecke.pebble.template;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GlobalContext implements Map<String, Object> {

  private final ScopeChain scopeChain;

  public GlobalContext(ScopeChain scopeChain) {
    this.scopeChain = scopeChain;
  }

  @Override
  public Object get(Object key) {
    List<Scope> globalScopes = this.scopeChain.getGlobalScopes();
    String keyAsString = String.valueOf(key);
    for (Scope scope : globalScopes) {
      Object result = scope.get(keyAsString);
      if (result != null) {
        return result;
      } else if (scope.containsKey(keyAsString)) {
        return null;
      }
    }

    return null;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public int size() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsKey(Object key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsValue(Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object put(String key, Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object remove(Object key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void putAll(Map<? extends String, ?> m) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<String> keySet() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<Object> values() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<Entry<String, Object>> entrySet() {
    throw new UnsupportedOperationException();
  }
}
