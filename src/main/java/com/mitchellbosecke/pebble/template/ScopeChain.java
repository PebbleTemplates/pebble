/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.template;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.mitchellbosecke.pebble.error.AttributeNotFoundException;

public class ScopeChain {

    private LinkedList<Scope> stack = new LinkedList<>();

    public ScopeChain() {
        pushScope();
    }

    public ScopeChain deepCopy() {
        ScopeChain copy = new ScopeChain();

        for (Scope originalScope : stack) {
            copy.stack.add(originalScope.deepCopy());
        }
        return copy;
    }

    public void pushScope() {
        pushScope(new Scope());
    }

    public void pushScope(Scope scope) {
        stack.push(scope);
    }

    public void pushLocalScope() {
        Scope local = new Scope();
        local.setLocal(true);
        stack.push(local);
    }

    public void popScope() {
        stack.pop();
    }

    public void putAll(Map<String, Object> objects) {
        stack.peek().putAll(objects);
    }

    public void put(String key, Object value) {
        stack.peek().put(key, value);
    }

    public Object get(Object key, boolean isStrictVariables) throws AttributeNotFoundException {
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
