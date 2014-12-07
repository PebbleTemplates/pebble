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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.mitchellbosecke.pebble.error.AttributeNotFoundException;

public class ScopeChain {

    private LinkedList<Scope> stack = new LinkedList<>();

    public ScopeChain(Map<String, Object> map) {
        pushScope(map);
    }

    /**
     * Creates a deep copy of the ScopeChain. This is used for the parallel tag
     * because every new thread should have a "snapshot" of the scopes, i.e. if
     * one thread adds a new object to a scope, it should not be available to
     * the other threads.
     * 
     * @return
     */
    public ScopeChain deepCopy() {
        ScopeChain copy = new ScopeChain(new HashMap<String, Object>());

        for (Scope originalScope : stack) {
            copy.stack.add(originalScope.shallowCopy());
        }
        return copy;
    }

    public void pushScope(Map<String, Object> map) {
        Scope scope = new Scope(map, false);
        stack.push(scope);
    }

    public void pushLocalScope() {
        Scope scope = new Scope(new HashMap<String, Object>(), true);
        stack.push(scope);
    }

    public void popScope() {
        stack.pop();
    }


    public void put(String key, Object value) {
        stack.peek().put(key, value);
    }

    public Object get(String key, boolean isStrictVariables) throws AttributeNotFoundException {
        Object result = null;
        boolean found = false;

        Scope scope;

        Iterator<Scope> iterator = stack.iterator();

        while (iterator.hasNext() && !found) {
            scope = iterator.next();

            if (scope.containsKey(key)) {
                found = true;
                result = scope.get(key);
            }
            if (scope.isLocal()) {
                break;
            }
        }

        if (!found && isStrictVariables) {
            throw new AttributeNotFoundException(null, String.format(
                    "Variable [%s] does not exist and strict variables is set to true.", String.valueOf(key)));
        }
        return result;
    }

}
