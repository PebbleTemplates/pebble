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
