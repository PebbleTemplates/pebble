package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.expression.Expression;

public class PositionalArgumentNode implements Node {

	private final Expression<?> value;

	public PositionalArgumentNode(Expression<?> value) {
		this.value = value;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public Expression<?> getValueExpression() {
		return value;
	}

}
