/*******************************************************************************
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.utils.Pair;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class IfNode extends AbstractRenderableNode {

    private final List<Pair<Expression<?>, BodyNode>> conditionsWithBodies;

    private final BodyNode elseBody;

    public IfNode(int lineNumber, List<Pair<Expression<?>, BodyNode>> conditionsWithBodies) {
        this(lineNumber, conditionsWithBodies, null);
    }

    public IfNode(int lineNumber, List<Pair<Expression<?>, BodyNode>> conditionsWithBodies, BodyNode elseBody) {
        super(lineNumber);
        this.conditionsWithBodies = conditionsWithBodies;
        this.elseBody = elseBody;
    }

    @Override
    public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context) throws PebbleException,
            IOException {

        boolean satisfied = false;
        for (Pair<Expression<?>, BodyNode> ifStatement : conditionsWithBodies) {

            Expression<?> conditionalExpression = ifStatement.getLeft();

            try {

                Object result = conditionalExpression.evaluate(self, context);

                if (result != null) {
                    try {
                        satisfied = (Boolean) result;
                    } catch (ClassCastException ex) {
                        throw new PebbleException(ex, "Expected a Boolean in \"if\" statement", getLineNumber(), self.getName());
                    }
                }

            } catch (RuntimeException ex) {
                throw new PebbleException(ex, "Wrong operand(s) type in conditional expression", getLineNumber(), self.getName());
            }

            if (satisfied) {
                ifStatement.getRight().render(self, writer, context);
                break;
            }
        }

        if (!satisfied && elseBody != null) {
            elseBody.render(self, writer, context);
        }
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public List<Pair<Expression<?>, BodyNode>> getConditionsWithBodies() {
        return conditionsWithBodies;
    }

    public BodyNode getElseBody() {
        return elseBody;
    }

}
