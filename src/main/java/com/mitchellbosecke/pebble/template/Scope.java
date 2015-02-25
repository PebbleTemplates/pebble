/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.template;

import java.util.HashMap;
import java.util.Map;

/**
 * A scope is a map of variables. If it's a "local" scope that tells the
 * ScopeChain to stop looking for a variable if it can't find it in this
 * particular scope.
 * 
 * @author Mitchell
 * 
 */
public class Scope {

    private final boolean isLocal;

    private final Map<String, Object> backingMap;

    public Scope(Map<String, Object> backingMap, boolean isLocal) {
        this.backingMap = backingMap == null ? new HashMap<String, Object>() : backingMap;
        this.isLocal = isLocal;
    }

    /**
     * Creates a shallow copy of the Scope. This is used for the parallel tag
     * because every new thread should have a "snapshot" of the scopes, i.e. one
     * thread should not affect rendering output of another.
     * 
     * @return
     */
    public Scope shallowCopy() {

        Map<String, Object> backingMapCopy = new HashMap<>();
        backingMapCopy.putAll(backingMap);

        return new Scope(backingMapCopy, isLocal);
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void put(String key, Object value) {
        backingMap.put(key, value);
    }

    public Object get(String key) {
        return backingMap.get(key);
    }

    public boolean containsKey(String key) {
        return backingMap.containsKey(key);
    }

}
