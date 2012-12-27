package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.node.NodeExpression;

public class NodeExpressionGetAttribute extends NodeExpression {

	private final NodeExpression node;
	private final NodeExpression attribute;

	public NodeExpressionGetAttribute(NodeExpression node,
			NodeExpression attribute, int lineNumber) {
		super(lineNumber);
		this.node = node;
		this.attribute = attribute;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.raw("getAttribute(").subcompile(node).raw(",\"")
				.subcompile(attribute).raw("\")");
	}

}
