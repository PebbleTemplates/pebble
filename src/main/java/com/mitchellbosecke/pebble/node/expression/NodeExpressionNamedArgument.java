package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.compiler.Compiler;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.NodeExpression;

public class NodeExpressionNamedArgument extends NodeExpression {

	private final NodeExpressionNewVariableName name;

	private final NodeExpression value;

	public NodeExpressionNamedArgument(NodeExpressionNewVariableName name, NodeExpression value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public void compile(Compiler compiler) {
		compiler.subcompile(value);
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public NodeExpression getValue() {
		return value;
	}

	public NodeExpressionNewVariableName getName() {
		return name;
	}

}
