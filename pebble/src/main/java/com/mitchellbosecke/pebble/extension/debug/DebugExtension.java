/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.extension.debug;

import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.NodeVisitorFactory;
import java.util.ArrayList;
import java.util.List;

public class DebugExtension extends AbstractExtension {

  private final PrettyPrintNodeVisitorFactory prettyPrinter = new PrettyPrintNodeVisitorFactory();

  public List<NodeVisitorFactory> getNodeVisitors() {
    List<NodeVisitorFactory> visitors = new ArrayList<>();
    visitors.add(this.prettyPrinter);
    return visitors;
  }

  public String toString() {
    return this.prettyPrinter.toString();
  }
}
