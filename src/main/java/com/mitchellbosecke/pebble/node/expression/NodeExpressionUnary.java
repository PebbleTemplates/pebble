package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.node.Node;

public abstract class NodeExpressionUnary extends NodeExpressionOperator {

	private final Node node;
	
	public NodeExpressionUnary(int lineNumber, Node node) {
		super(lineNumber);
		this.node = node;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.raw("(");
		operator(compiler);
		compiler.subcompile(node).raw(")");
	}

}
