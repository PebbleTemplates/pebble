/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.template.EvaluationContextImpl;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.utils.Pair;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import static com.mitchellbosecke.pebble.utils.TypeUtils.compatibleCast;

public class IfNode extends AbstractRenderableNode {

  private final List<Pair<Expression<?>, BodyNode>> conditionsWithBodies;

  private final BodyNode elseBody;

  public IfNode(int lineNumber, List<Pair<Expression<?>, BodyNode>> conditionsWithBodies) {
    this(lineNumber, conditionsWithBodies, null);
  }

  public IfNode(int lineNumber, List<Pair<Expression<?>, BodyNode>> conditionsWithBodies,
      BodyNode elseBody) {
    super(lineNumber);
    this.conditionsWithBodies = conditionsWithBodies;
    this.elseBody = elseBody;
  }

  @Override
  public void render(PebbleTemplateImpl self, Writer writer, EvaluationContextImpl context)
      throws IOException {

    boolean satisfied = false;
    for (Pair<Expression<?>, BodyNode> ifStatement: this.conditionsWithBodies) {

      Expression<?> conditionalExpression = ifStatement.getLeft();

      try {

        Object result = conditionalExpression.evaluate(self, context);

        if (result != null) {
          if (result instanceof Boolean
                  || result instanceof Number
                  || result instanceof String) {
            satisfied = compatibleCast(result, Boolean.class);
          } else {
            throw new PebbleException(
                      null,
                      String.format(
                              "Unsupported value type %s. Expected Boolean, String, Number in \"if\" statement",
                              result.getClass().getSimpleName()),
                      this.getLineNumber(),
                      self.getName());
          }

        } else if (context.isStrictVariables()) {
          throw new PebbleException(null,
              "null value given to if statement and strict variables is set to true",
              this.getLineNumber(), self.getName());
        }

      } catch (RuntimeException ex) {
        throw new PebbleException(ex, "Wrong operand(s) type in conditional expression",
            this.getLineNumber(), self.getName());
      }

      if (satisfied) {
        ifStatement.getRight().render(self, writer, context);
        break;
      }
    }

    if (!satisfied && this.elseBody != null) {
      this.elseBody.render(self, writer, context);
    }
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  public List<Pair<Expression<?>, BodyNode>> getConditionsWithBodies() {
    return this.conditionsWithBodies;
  }

  public BodyNode getElseBody() {
    return this.elseBody;
  }

}
