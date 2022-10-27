/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.node;

import io.pebbletemplates.pebble.extension.NodeVisitor;
import io.pebbletemplates.pebble.node.expression.Expression;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.PebbleTemplateImpl;

public class FunctionOrMacroNameNode implements Expression<String> {

  private final String name;

  private final int lineNumber;

  public FunctionOrMacroNameNode(String name, int lineNumber) {
    this.name = name;
    this.lineNumber = lineNumber;
  }

  @Override
  public String evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  public String getName() {
    return this.name;
  }

  @Override
  public int getLineNumber() {
    return this.lineNumber;
  }

}
