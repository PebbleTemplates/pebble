/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.error.RootAttributeNotFoundException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.template.ScopeChain;

public class ContextVariableExpression implements Expression<Object> {

  protected final String name;

  private final int lineNumber;

  public ContextVariableExpression(String name, int lineNumber) {
    this.name = name;
    this.lineNumber = lineNumber;
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  public String getName() {
    return this.name;
  }

  @Override
  public Object evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
    ScopeChain scopeChain = context.getScopeChain();
    Object result = scopeChain.get(this.name);
    if (result == null && context.isStrictVariables() && !scopeChain.containsKey(this.name)) {
      throw new RootAttributeNotFoundException(null, String.format(
          "Root attribute [%s] does not exist or can not be accessed and strict variables is set to true.",
          this.name), this.name, this.lineNumber, self.getName());
    }
    return result;
  }

  @Override
  public int getLineNumber() {
    return this.lineNumber;
  }

  @Override
  public String toString() {
    return String.format("[%s]", this.name);
  }

}
