package com.mitchellbosecke.pebble.node.expression;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class LiteralLongExpression implements Expression<Long> {

	private final Long value;

	public LiteralLongExpression(Long value) {
		this.value = value;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Long evaluate(PebbleTemplateImpl self, EvaluationContext context) throws PebbleException {
		return value;
	}

}
