package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.node.NodeExpression;

public class NodeExpressionConstant extends NodeExpression {

	private final Object value;

	public NodeExpressionConstant(Object value, int lineNumber) {
		super(lineNumber);
		this.value = value;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.raw(String.valueOf(value));
	}

}
