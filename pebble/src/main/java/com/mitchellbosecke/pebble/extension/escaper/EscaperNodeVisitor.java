/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.extension.escaper;

import com.mitchellbosecke.pebble.extension.AbstractNodeVisitor;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.node.AutoEscapeNode;
import com.mitchellbosecke.pebble.node.NamedArgumentNode;
import com.mitchellbosecke.pebble.node.PrintNode;
import com.mitchellbosecke.pebble.node.expression.BlockFunctionExpression;
import com.mitchellbosecke.pebble.node.expression.ConcatenateExpression;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.node.expression.FilterExpression;
import com.mitchellbosecke.pebble.node.expression.FilterInvocationExpression;
import com.mitchellbosecke.pebble.node.expression.LiteralStringExpression;
import com.mitchellbosecke.pebble.node.expression.ParentFunctionExpression;
import com.mitchellbosecke.pebble.node.expression.TernaryExpression;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class EscaperNodeVisitor extends AbstractNodeVisitor {

  private final LinkedList<String> strategies = new LinkedList<>();

  private final LinkedList<Boolean> active = new LinkedList<>();

  public EscaperNodeVisitor(PebbleTemplateImpl template, boolean autoEscapting) {
    super(template);
    this.pushAutoEscapeState(autoEscapting);
  }

  @Override
  public void visit(PrintNode node) {
    Expression<?> expression = node.getExpression();

    if (expression instanceof TernaryExpression) {
      TernaryExpression ternary = (TernaryExpression) expression;
      Expression<?> left = ternary.getExpression2();
      Expression<?> right = ternary.getExpression3();
      if (this.isUnsafe(left)) {
        ternary.setExpression2(this.escape(left));
      }
      if (this.isUnsafe(right)) {
        ternary.setExpression3(this.escape(right));
      }
    } else {
      if (this.isUnsafe(expression)) {
        node.setExpression(this.escape(expression));
      }
    }
  }

  @Override
  public void visit(AutoEscapeNode node) {
    this.active.push(node.isActive());
    this.strategies.push(node.getStrategy());

    node.getBody().accept(this);

    this.active.pop();
    this.strategies.pop();
  }

  /**
   * Simply wraps the input expression with a {@link EscapeFilter}.
   */
  private Expression<?> escape(Expression<?> expression) {

    /*
     * Build the arguments to the escape filter. The arguments will just
     * include the strategy being used.
     */
    List<NamedArgumentNode> namedArgs = new ArrayList<>();
    if (!this.strategies.isEmpty() && this.strategies.peek() != null) {
      String strategy = this.strategies.peek();
      namedArgs.add(new NamedArgumentNode("strategy",
          new LiteralStringExpression(strategy, expression.getLineNumber())));
    }
    ArgumentsNode args = new ArgumentsNode(null, namedArgs, expression.getLineNumber());

    /*
     * Create the filter invocation with the newly created named arguments.
     */
    FilterInvocationExpression filter = new FilterInvocationExpression("escape", args,
        expression.getLineNumber());

    /*
     * The given expression and the filter invocation now become a binary
     * expression which is what is returned.
     */
    FilterExpression binary = new FilterExpression();
    binary.setLeft(expression);
    binary.setRight(filter);
    return binary;
  }

  private boolean isUnsafe(Expression<?> expression) {

    // check whether the autoescaper is even active
    if (this.active.peek() == Boolean.FALSE) {
      return false;
    }

    boolean unsafe = true;

    // string literals are safe
    if (expression instanceof LiteralStringExpression) {
      unsafe = false;
    } else if (expression instanceof ParentFunctionExpression
        || expression instanceof BlockFunctionExpression) {
      unsafe = false;
    } else if (this.isSafeConcatenateExpr(expression)) {
      unsafe = false;
    }

    return unsafe;
  }

  /**
   * Returns true if {@code expr} is a {@link ConcatenateExpression} made up of two {@link
   * LiteralStringExpression}s.
   */
  private boolean isSafeConcatenateExpr(Expression<?> expr) {
    if (!(expr instanceof ConcatenateExpression)) {
      return false;
    }
    ConcatenateExpression cexpr = (ConcatenateExpression) expr;
    return cexpr.getLeftExpression() instanceof LiteralStringExpression
        && cexpr.getRightExpression() instanceof LiteralStringExpression;
  }

  public void pushAutoEscapeState(boolean auto) {
    this.active.push(auto);
  }

}
