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
import io.pebbletemplates.pebble.template.ScopeChain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScopeChainTest {

  @Test
  void testSet() throws PebbleException {
    ScopeChain scopeChain = new ScopeChain();
    scopeChain.pushScope();
    scopeChain.set("key", "value");
    assertEquals("value", scopeChain.get("key"));
    scopeChain.pushScope();
    scopeChain.set("key", "value2");
    assertEquals("value2", scopeChain.get("key"));
    scopeChain.popScope();
    assertEquals("value2", scopeChain.get("key"));
    scopeChain.pushLocalScope();
    scopeChain.set("key", "value3");
    assertEquals("value3", scopeChain.get("key"));
    scopeChain.popScope();
    assertEquals("value2", scopeChain.get("key"));
  }

  @Test
  void testGetValueWithLocalScopeFirstInChainAndValueInAnotherScope() {
    ScopeChain scopeChain = new ScopeChain();
    scopeChain.pushScope();
    scopeChain.set("key", "value");

    scopeChain.pushLocalScope();
    scopeChain.set("key2", "value2");

    assertNull(scopeChain.get("key"));
    assertEquals("value2", scopeChain.get("key2"));
  }

  @Test
  void testGetValueWithLocalScopeNotFirstInChainAndValueInAnotherScope() {
    ScopeChain scopeChain = new ScopeChain();
    scopeChain.pushScope();
    scopeChain.set("key", "value");

    scopeChain.pushLocalScope();
    scopeChain.set("key2", "value2");

    scopeChain.pushScope();
    scopeChain.set("key3", "value3");

    assertNull(scopeChain.get("key"));
    assertEquals("value2", scopeChain.get("key2"));
    assertEquals("value3", scopeChain.get("key3"));
  }

  @Test
  void testContainsKeyWithLocalScopeFirstInChainAndValueInAnotherScope() {
    ScopeChain scopeChain = new ScopeChain();
    scopeChain.pushScope();
    scopeChain.set("key", "value");

    scopeChain.pushLocalScope();
    scopeChain.set("key2", "value2");

    assertFalse(scopeChain.containsKey("key"));
    assertTrue(scopeChain.containsKey("key2"));
  }

  @Test
  void testContainsKeyWithLocalScopeNotFirstInChainAndValueInAnotherScope() {
    ScopeChain scopeChain = new ScopeChain();
    scopeChain.pushScope();
    scopeChain.set("key", "value");

    scopeChain.pushLocalScope();
    scopeChain.set("key2", "value2");

    scopeChain.pushScope();
    scopeChain.set("key3", "value3");

    assertFalse(scopeChain.containsKey("key"));
    assertTrue(scopeChain.containsKey("key2"));
    assertTrue(scopeChain.containsKey("key3"));
  }
}
