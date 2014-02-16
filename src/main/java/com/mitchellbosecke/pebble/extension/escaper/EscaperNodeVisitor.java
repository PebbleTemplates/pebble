package com.mitchellbosecke.pebble.extension.escaper;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.mitchellbosecke.pebble.compiler.BaseNodeVisitor;
import com.mitchellbosecke.pebble.node.NodeAutoEscape;
import com.mitchellbosecke.pebble.node.NodeExpression;
import com.mitchellbosecke.pebble.node.NodePrint;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionBlockReferenceAndFunction;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionConstant;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionFilterInvocation;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionFunctionOrMacroInvocation;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionNamedArgument;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionNamedArguments;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionNewVariableName;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionParentFunction;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionString;
import com.mitchellbosecke.pebble.node.expression.NodeExpressionTernary;
import com.mitchellbosecke.pebble.node.expression.binary.NodeExpressionBinaryFilter;

public class EscaperNodeVisitor extends BaseNodeVisitor {

	private final Stack<String> strategies = new Stack<>();

	private final Stack<Boolean> active = new Stack<>();

	private final List<String> safeFilters = new ArrayList<>();

	public EscaperNodeVisitor() {
		safeFilters.add("raw");
		safeFilters.add("escape");
		safeFilters.add("date");
	}

	@Override
	public void visit(NodePrint node) {
		NodeExpression expression = node.getExpression();
		if (!isSafe(expression)) {
			node.setExpression(escape(expression));
		}
	}

	@Override
	public void visit(NodeExpressionTernary node) {
		NodeExpression left = node.getExpression2();
		NodeExpression right = node.getExpression3();
		if (!isSafe(left)) {
			node.setExpression2(escape(left));
		}
		if (!isSafe(right)) {
			node.setExpression3(escape(right));
		}
	}

	@Override
	public void visit(NodeAutoEscape node) {
		active.push(node.isActive());
		strategies.push(node.getStrategy());

		node.getBody().accept(this);

		active.pop();
		strategies.pop();
	}

	private NodeExpression escape(NodeExpression expression) {

		int lineNumber = expression.getLineNumber();

		/*
		 * Build the arguments to the escape filter. The arguments will just
		 * include the strategy being used.
		 */
		List<NodeExpressionNamedArgument> namedArgs = new ArrayList<>();
		if (!strategies.isEmpty() && strategies.peek() != null) {
			String strategy = strategies.peek();
			NodeExpressionNewVariableName name = new NodeExpressionNewVariableName(lineNumber, "strategy");
			NodeExpression value = new NodeExpressionString(lineNumber, strategy);
			namedArgs.add(new NodeExpressionNamedArgument(name, value));
		}
		NodeExpressionNamedArguments args = new NodeExpressionNamedArguments(lineNumber, namedArgs);

		/*
		 * Create the filter invocation with the newly created named arguments.
		 */
		NodeExpressionConstant filterName = new NodeExpressionConstant(lineNumber, "escape");
		NodeExpressionFilterInvocation filter = new NodeExpressionFilterInvocation(expression.getLineNumber(),
				filterName, args);

		/*
		 * The given expression and the filter invocation now become a binary
		 * expression which is what is returned.
		 */
		NodeExpressionBinaryFilter binary = new NodeExpressionBinaryFilter();
		binary.setLeft(expression);
		binary.setRight(filter);
		return binary;
	}

	private boolean isSafe(NodeExpression expression) {

		// check whether the autoescaper is even active
		if (!active.isEmpty() && active.peek() == false) {
			return true;
		}

		boolean safe = false;

		// string literals are safe
		if (expression instanceof NodeExpressionString) {
			safe = true;
		}
		// function and macro calls are considered safe
		else if (expression instanceof NodeExpressionFunctionOrMacroInvocation
				|| expression instanceof NodeExpressionParentFunction
				|| expression instanceof NodeExpressionBlockReferenceAndFunction) {
			safe = true;
		} else if (expression instanceof NodeExpressionBinaryFilter) {

			// certain filters do not need to be escaped
			NodeExpressionBinaryFilter binary = (NodeExpressionBinaryFilter) expression;
			NodeExpressionFilterInvocation filterInvocation = (NodeExpressionFilterInvocation) binary
					.getRightExpression();
			String filterName = (String) filterInvocation.getFilterName().getValue();

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
