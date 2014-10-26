/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension.debug;

import java.util.ArrayList;
import java.util.List;

import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.NodeVisitor;

public class DebugExtension extends AbstractExtension {

    private final PrettyPrintNodeVisitor prettyPrinter = new PrettyPrintNodeVisitor();

    public List<NodeVisitor> getNodeVisitors() {
        List<NodeVisitor> visitors = new ArrayList<>();
        visitors.add(prettyPrinter);
        return visitors;
    }

    public String toString() {
        return prettyPrinter.toString();
    }
}
