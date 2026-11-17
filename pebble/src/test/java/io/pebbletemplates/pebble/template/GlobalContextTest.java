package io.pebbletemplates.pebble.template;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GlobalContextTest {

  @Test
  void resolvesGlobalValuesFromInnermostToOutermostAndStringifiesKeys() {
    ScopeChain scopeChain = new ScopeChain();
    Map<String, Object> outer = new HashMap<>();
    outer.put("outerOnly", "outer");
    outer.put("shared", "outer value");
    outer.put("42", "stringified key");
    scopeChain.pushScope(outer);

    Map<String, Object> inner = new HashMap<>();
    inner.put("shared", "inner value");
    scopeChain.pushScope(inner);

    GlobalContext globals = new GlobalContext(scopeChain);
    assertEquals("outer", globals.get("outerOnly"));
    assertEquals("inner value", globals.get("shared"));
    assertEquals("stringified key", globals.get(42));
    assertNull(globals.get("missing"));
    assertFalse(globals.isEmpty());
  }

  @Test
  void nullGlobalShadowsOuterValue() {
    ScopeChain scopeChain = new ScopeChain();
    Map<String, Object> outer = new HashMap<>();
    outer.put("shadowed", "outer");
    scopeChain.pushScope(outer);

    Map<String, Object> inner = new HashMap<>();
    inner.put("shadowed", null);
    scopeChain.pushScope(inner);

    GlobalContext globals = new GlobalContext(scopeChain);
    assertNull(globals.get("shadowed"));
  }

  @Test
  void localScopeSeparatesGlobalScopes() {
    ScopeChain scopeChain = new ScopeChain();
    Map<String, Object> outer = new HashMap<>();
    outer.put("outsideLocal", "outer");
    scopeChain.pushScope(outer);

    scopeChain.pushLocalScope();
    Map<String, Object> inner = new HashMap<>();
    inner.put("insideLocal", "inner");
    scopeChain.pushScope(inner);

    GlobalContext globals = new GlobalContext(scopeChain);
    assertNull(globals.get("insideLocal"));
    assertEquals("outer", globals.get("outsideLocal"));
  }

  @Test
  void unsupportedMapOperationsFailExplicitly() {
    GlobalContext globals = new GlobalContext(new ScopeChain());

    assertThrows(UnsupportedOperationException.class, globals::size);
    assertThrows(UnsupportedOperationException.class, () -> globals.containsKey("key"));
    assertThrows(UnsupportedOperationException.class, () -> globals.containsValue("value"));
    assertThrows(UnsupportedOperationException.class, () -> globals.put("key", "value"));
    assertThrows(UnsupportedOperationException.class, () -> globals.remove("key"));
    assertThrows(UnsupportedOperationException.class, () -> globals.putAll(new HashMap<>()));
    assertThrows(UnsupportedOperationException.class, globals::clear);
    assertThrows(UnsupportedOperationException.class, globals::keySet);
    assertThrows(UnsupportedOperationException.class, globals::values);
    assertThrows(UnsupportedOperationException.class, globals::entrySet);
  }
}
