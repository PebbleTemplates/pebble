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

/**
 * The right hand side to the test expression.
 *
 * @author Mitchell
 */
public class TestInvocationExpression implements Expression<Object> {

  private final String testName;

  private final ArgumentsNode args;

  private final int lineNumber;

  @Override
  public Object evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
    throw new UnsupportedOperationException();
  }

  public TestInvocationExpression(int lineNumber, String testName, ArgumentsNode args) {
    this.testName = testName;
    this.args = args;
    this.lineNumber = lineNumber;
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  public ArgumentsNode getArgs() {
    return this.args;
  }

  public String getTestName() {
    return this.testName;
  }

  @Override
  public int getLineNumber() {
    return this.lineNumber;
  }
}
