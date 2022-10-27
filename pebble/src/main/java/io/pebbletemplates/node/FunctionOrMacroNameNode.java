/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.node;

import io.pebbletemplates.extension.NodeVisitor;
import io.pebbletemplates.node.expression.Expression;
import io.pebbletemplates.template.EvaluationContextImpl;
import io.pebbletemplates.template.PebbleTemplateImpl;

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
