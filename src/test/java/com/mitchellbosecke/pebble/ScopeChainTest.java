/*******************************************************************************
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.ScopeChain;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ScopeChainTest extends AbstractTest {

    @Test
    public void testSet() throws PebbleException, IOException {
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
}
