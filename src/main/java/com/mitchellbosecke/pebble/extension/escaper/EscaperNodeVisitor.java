/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension.escaper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.mitchellbosecke.pebble.extension.AbstractNodeVisitor;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.node.AutoEscapeNode;
import com.mitchellbosecke.pebble.node.NamedArgumentNode;
import com.mitchellbosecke.pebble.node.PrintNode;
import com.mitchellbosecke.pebble.node.expression.BlockFunctionExpression;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.node.expression.FilterExpression;
import com.mitchellbosecke.pebble.node.expression.FilterInvocationExpression;
import com.mitchellbosecke.pebble.node.expression.FunctionOrMacroInvocationExpression;
import com.mitchellbosecke.pebble.node.expression.LiteralStringExpression;
import com.mitchellbosecke.pebble.node.expression.ParentFunctionExpression;
import com.mitchellbosecke.pebble.node.expression.TernaryExpression;

public class EscaperNodeVisitor extends AbstractNodeVisitor {

	private final LinkedList<String> strategies = new LinkedList<>();

	private final LinkedList<Boolean> active = new LinkedList<>();

	private final List<String> safeFilters = new ArrayList<>();

	public EscaperNodeVisitor() {
		safeFilters.add("raw");
		safeFilters.add("escape");
		safeFilters.add("date");
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

	private Expression<?> escape(Expression<?> expression) {

		/*
		 * Build the arguments to the escape filter. The arguments will just
		 * include the strategy being used.
		 */
		List<NamedArgumentNode> namedArgs = new ArrayList<>();
		if (!strategies.isEmpty() && strategies.peek() != null) {
			String strategy = strategies.peek();
			namedArgs.add(new NamedArgumentNode("strategy", new LiteralStringExpression(strategy)));
		}
		ArgumentsNode args = new ArgumentsNode(null, namedArgs);

		/*
		 * Create the filter invocation with the newly created named arguments.
		 */
		FilterInvocationExpression filter = new FilterInvocationExpression("escape", args);

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
		if (!active.isEmpty() && active.peek() == false) {
			return true;
		}

		boolean safe = false;

		// string literals are safe
		if (expression instanceof LiteralStringExpression) {
			safe = true;
		}
		// function and macro calls are considered safe
		else if (expression instanceof FunctionOrMacroInvocationExpression
				|| expression instanceof ParentFunctionExpression || expression instanceof BlockFunctionExpression) {
			safe = true;
		} else if (expression instanceof FilterExpression) {

			// certain filters do not need to be escaped
			FilterExpression binary = (FilterExpression) expression;
			FilterInvocationExpression filterInvocation = (FilterInvocationExpression) binary.getRightExpression();
			String filterName = filterInvocation.getFilterName();

			if (safeFilters.contains(filterName)) {
				safe = true;
			}
		}

		return safe;
	}

	public void addSafeFilter(String filter) {
		this.safeFilters.add(filter);
	}

	public void pushAutoEscapeState(boolean auto) {
		active.push(auto);
	}

}
