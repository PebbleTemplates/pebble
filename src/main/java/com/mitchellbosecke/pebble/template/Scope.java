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

/**
 * A scope is a map of variables. If it's a "local" scope that tells the
 * ScopeChain to stop looking for a variable if it can't find it in this
 * particular scope.
 * 
 * @author Mitchell
 * 
 */
public class Scope extends HashMap<String, Object> {

    private static final long serialVersionUID = -3220073691105236100L;

    private boolean isLocal;

    public Scope() {

    }

    /**
     * Creates a shallow copy of the Scope. This is used for the parallel tag
     * because every new thread should have a "snapshot" of the scopes, i.e. one
     * thread should not affect rendering output of another.
     * 
     * @return
     */
    public Scope shallowCopy() {
        Scope copy = new Scope();
        copy.putAll(this);
        copy.setLocal(isLocal);
        return copy;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean isLocal) {
        this.isLocal = isLocal;
    }

}
