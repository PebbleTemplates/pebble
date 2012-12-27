package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.compiler.Compiler;

public class NodePrint extends AbstractNode implements DisplayableNode{

	private final NodeExpression expression;

	public NodePrint(NodeExpression expression, int lineNumber) {
		super(lineNumber);
		this.expression = expression;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.raw("\n").write("append(String.valueOf(").subcompile(expression)
				.raw("));");
	}

}
