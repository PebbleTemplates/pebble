package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class LiteralBooleanExpression implements Expression<Boolean> {

	private final Boolean value;

	public LiteralBooleanExpression(Boolean value) {
		this.value = value;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Boolean evaluate(PebbleTemplateImpl self, EvaluationContext context) throws PebbleException {
		return value;
	}

}
