/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import java.util.Map;

public class FunctionOrMacroInvocationExpression implements Expression<Object> {

  private final String functionName;

  private final ArgumentsNode args;

  private final int lineNumber;

  public FunctionOrMacroInvocationExpression(String functionName, ArgumentsNode arguments,
      int lineNumber) {
    this.functionName = functionName;
    this.args = arguments;
    this.lineNumber = lineNumber;
  }

  @Override
  public Object evaluate(PebbleTemplateImpl self, EvaluationContextImpl context) {
    Function function = context.getExtensionRegistry().getFunction(this.functionName);
    if (function != null) {
      return this.applyFunction(self, context, function, this.args);
    }
    return self.macro(context, this.functionName, this.args, false, this.lineNumber);
  }

  private Object applyFunction(PebbleTemplateImpl self, EvaluationContextImpl context,
      Function function, ArgumentsNode args) {
    Map<String, Object> namedArguments = args.getArgumentMap(self, context, function);
    return function.execute(namedArguments, self, context, this.getLineNumber());
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  public String getFunctionName() {
    return this.functionName;
  }

  public ArgumentsNode getArguments() {
    return this.args;
  }

  @Override
  public int getLineNumber() {
    return this.lineNumber;
  }

  @Override
  public String toString() {
    return String.format("%s%s", this.functionName, this.args);
  }

}
