/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.extension.core;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.AbstractNodeVisitor;
import io.pebbletemplates.pebble.node.BlockNode;
import io.pebbletemplates.pebble.node.MacroNode;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;

public class MacroAndBlockRegistrantNodeVisitor extends AbstractNodeVisitor {

  public MacroAndBlockRegistrantNodeVisitor(PebbleTemplateImpl template) {
    super(template);
  }

  @Override
  public void visit(BlockNode node) {
    this.getTemplate().registerBlock(node.getBlock());
    super.visit(node);
  }

  @Override
  public void visit(MacroNode node) {
    try {
      this.getTemplate().registerMacro(node.getMacro());
    } catch (PebbleException e) {
      throw new RuntimeException(e);
    }
    super.visit(node);
  }
}
