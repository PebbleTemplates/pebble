/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A stack data structure used to represent the scope of variables that are currently accessible.
 * Pushing a new scope will allow the template to add variables with names of pre-existing variables
 * without overriding the originals; to access the original variables you would pop the scope
 * again.
 */
public class ScopeChain {

  /**
   * The stack of scopes
   */
  private LinkedList<Scope> stack = new LinkedList<>();

  /**
   * Constructs an empty scope chain without any known scopes.
   */
  public ScopeChain() {
  }

  /**
   * Creates a deep copy of the ScopeChain. This is used for the parallel tag because every new
   * thread should have a "snapshot" of the scopes, i.e. if one thread adds a new object to a scope,
   * it should not be available to the other threads.
   * <p>
   * This will construct a new scope chain and new scopes but it will continue to have references to
   * the original user-provided variables. This is why it is important for the user to only provide
   * thread-safe variables when using the "parallel" tag.
   *
   * @return A copy of the scope chain
   */
  public ScopeChain deepCopy() {
    ScopeChain copy = new ScopeChain();

    for (Scope originalScope : this.stack) {
      copy.stack.add(originalScope.shallowCopy());
    }
    return copy;
  }

  /**
   * Adds an empty non-local scope to the scope chain
   */
  public void pushScope() {
    this.pushScope(new HashMap<>());
  }

  /**
   * Adds a new non-local scope to the scope chain
   *
   * @param map The known variables of this scope.
   */
  public void pushScope(Map<String, Object> map) {
    Scope scope = new Scope(map, false);
    this.stack.push(scope);
  }

  /**
   * Adds a new local scope to the scope chain
   */
  public void pushLocalScope() {
    Scope scope = new Scope(new HashMap<>(), true);
    this.stack.push(scope);
  }

  /**
   * Pops the most recent scope from the scope chain.
   */
  public void popScope() {
    this.stack.pop();
  }

  /**
   * Adds a variable to the current scope.
   *
   * @param key The name of the variable
   * @param value The value of the variable
   */
  public void put(String key, Object value) {
    this.stack.peek().put(key, value);
  }

  /**
   * Retrieves a variable from the scope chain, starting at the current scope and working it's way
   * up all visible scopes.
   *
   * @param key The name of the variable
   * @return The value of the variable
   */
  public Object get(String key) {
    /*
     * The majority of time, the requested variable will be in the first
     * scope so we do a quick lookup in that scope before attempting to
     * create an iterator, etc. This is solely for performance.
     * null values must not be handled as "not present".
     */
    Scope scope = this.stack.getFirst();
    Object result = scope.get(key);
    if (result != null) {
      return result;
    }

    if (this.stack.size() > 1) {
      if (scope.isLocal() || scope.containsKey(key)) {
        // key could be defined with null and override another value below in the stack
        return null;
      }
      Iterator<Scope> iterator = this.stack.iterator();
      // account for the first lookup we did
      iterator.next();

      while (iterator.hasNext()) {
        scope = iterator.next();
        result = scope.get(key);
        if (result != null) {
          return result;
        } else if (scope.isLocal() || scope.containsKey(key)) {
          // null value
          return null;
        }
      }
    }

    return null;
  }

  /**
   * This method checks if the given {@code key} does exists within the scope chain.
   *
   * @param key the for which the the check should be executed for.
   * @return {@code true} when the key does exists or {@code false} when the given key does not
   * exists.
   */
  public boolean containsKey(String key) {

    /*
     * The majority of time, the requested variable will be in the first
     * scope so we do a quick lookup in that scope before attempting to
     * create an iterator, etc. This is solely for performance.
     */
    Scope scope = this.stack.getFirst();
    if (scope.containsKey(key)) {
      return true;
    }
    if (scope.isLocal()) {
      return false;
    }

    Iterator<Scope> iterator = this.stack.iterator();

    // account for the first lookup we did
    iterator.next();

    while (iterator.hasNext()) {
      scope = iterator.next();

      if (scope.containsKey(key)) {
        return true;
      }
      if (scope.isLocal()) {
        return false;
      }
    }

    return false;
  }

  /**
   * Checks if the current scope contains a variable without then looking up the scope chain.
   *
   * @param variableName The name of the variable
   * @return Whether or not the variable exists in the current scope
   */
  public boolean currentScopeContainsVariable(String variableName) {
    return this.stack.getFirst().containsKey(variableName);
  }

  /**
   * Sets the value of a variable in the first scope in the chain that already contains the
   * variable; adds a variable to the current scope if an existing variable is not found.
   *
   * @param key The name of the variable
   * @param value The value of the variable
   */
  public void set(String key, Object value) {
    /*
     * The majority of time, the requested variable will be in the first
     * scope so we do a quick lookup in that scope before attempting to
     * create an iterator, etc. This is solely for performance.
     */
    Scope scope = this.stack.getFirst();
    if (scope.isLocal() || scope.containsKey(key)) {
      scope.put(key, value);
      return;
    }

    Iterator<Scope> iterator = this.stack.iterator();

    // account for the first lookup we did
    iterator.next();

    while (iterator.hasNext()) {
      scope = iterator.next();

      if (scope.isLocal() || scope.containsKey(key)) {
        scope.put(key, value);
        return;
      }
    }

    // no existing variable, create a new one
    this.put(key, value);
  }

  public List<Scope> getGlobalScopes() {
    List<Scope> globalScopes = new ArrayList<>();
    Iterator<Scope> iterator = this.stack.iterator();
    while (iterator.hasNext()) {
      Scope scope = iterator.next();
      if (scope.isLocal()) {
        globalScopes.clear();
      } else {
        globalScopes.add(scope);
      }
    }

    return globalScopes;
  }
}
