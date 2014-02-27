package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.expression.Expression;

public class NamedArgumentNode implements Node {

	private final Expression<?> value;

	private final String name;

	public NamedArgumentNode(String name, Expression<?> value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	public Expression<?> getValueExpression() {
		return value;
	}

	public String getName() {
		return name;
	}

}
