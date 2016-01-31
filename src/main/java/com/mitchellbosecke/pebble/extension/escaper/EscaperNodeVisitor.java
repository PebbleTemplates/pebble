/*******************************************************************************
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension.escaper;

import com.mitchellbosecke.pebble.extension.AbstractNodeVisitor;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.node.AutoEscapeNode;
import com.mitchellbosecke.pebble.node.NamedArgumentNode;
import com.mitchellbosecke.pebble.node.PrintNode;
import com.mitchellbosecke.pebble.node.expression.*;
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
            if (!isSafe(left)) {
                ternary.setExpression2(escape(left));
            }
            if (!isSafe(right)) {
                ternary.setExpression3(escape(right));
            }
        } else {
            if (!isSafe(expression)) {
                node.setExpression(escape(expression));
            }
        }
    }

    @Override
    public void visit(AutoEscapeNode node) {
        active.push(node.isActive());
        strategies.push(node.getStrategy());

        node.getBody().accept(this);

        active.pop();
        strategies.pop();
    }

    /**
     * Simply wraps the input expression with a {@link EscapeFilter}.
     * @param expression
     * @return
     */
    private Expression<?> escape(Expression<?> expression) {

        /*
         * Build the arguments to the escape filter. The arguments will just
         * include the strategy being used.
         */
        List<NamedArgumentNode> namedArgs = new ArrayList<>();
        if (!strategies.isEmpty() && strategies.peek() != null) {
            String strategy = strategies.peek();
            namedArgs.add(new NamedArgumentNode("strategy", new LiteralStringExpression(strategy, expression.getLineNumber())));
        }
        ArgumentsNode args = new ArgumentsNode(null, namedArgs, expression.getLineNumber());

        /*
         * Create the filter invocation with the newly created named arguments.
         */
        FilterInvocationExpression filter = new FilterInvocationExpression("escape", args, expression.getLineNumber());

        /*
         * The given expression and the filter invocation now become a binary
         * expression which is what is returned.
         */
        FilterExpression binary = new FilterExpression();
        binary.setLeft(expression);
        binary.setRight(filter);
        return binary;
    }

    private boolean isSafe(Expression<?> expression) {

        // check whether the autoescaper is even active
        if (!active.isEmpty() && !active.peek()) {
            return true;
        }

        boolean safe = false;

        // string literals are safe
        if (expression instanceof LiteralStringExpression) {
            safe = true;
        }
        else if (expression instanceof ParentFunctionExpression || expression instanceof BlockFunctionExpression) {
            safe = true;
        }

        return safe;
    }

    public void pushAutoEscapeState(boolean auto) {
        active.push(auto);
    }

}
